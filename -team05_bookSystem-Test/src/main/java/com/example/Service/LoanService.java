package com.example.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Dto.LoanResponseDto;
import com.example.Dto.ReviewResponseDto;
import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Entity.UserEntity;
import com.example.Repository.BookRepository;
import com.example.Repository.LoanRepository;
import com.example.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 1. 도서 대출 실행
     */
    @Transactional
    public String borrowBook(String userId, Integer bookId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다."));

        // 대출 가능 여부 체크
        if ("RENTED".equals(book.getStatus())) {
            return "이미 다른 사용자가 대여 중인 도서입니다.";
        }
        if ("DELETED".equals(book.getStatus())) {
            return "삭제(폐기)된 도서는 대출할 수 없습니다.";
        }

        if (user.getCurrentRentalCount() >= 3) {
            return "대출 가능 수량(3권)을 초과하였습니다.";
        }

        LoanEntity loan = new LoanEntity();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plusDays(14));
        loan.setStatus("BORROWED");
        loan.setOverdueDays(0); //[추가] 초기 연체일수는 0으로 설정

        loanRepository.save(loan);

        // 상태 업데이트
        book.setStatus("RENTED");
        book.setRentalHitCount(book.getRentalHitCount() + 1);
        user.setCurrentRentalCount(user.getCurrentRentalCount() + 1);

        return "[" + book.getTitle() + "] 대출 완료! 반납 기한: " + loan.getDueDate();
    }

    /**
     * 2. 도서 반납 및 리뷰 등록
     */
    @Transactional
    public String returnBook(Integer bookId, Double rating, String reviewContent) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

//        LoanEntity loan = loanRepository.findByBookAndStatus(book, "BORROWED")
//                .orElseThrow(() -> new IllegalArgumentException("대출 기록이 없거나 이미 반납되었습니다."));

        // [수정] 기존 'BORROWED' 뿐만 아니라 연체 중인 'OVERDUE' 상태도 반납 가능해야 함
        LoanEntity loan = loanRepository.findByBookAndStatusIn(book, List.of("BORROWED", "OVERDUE"))
                .orElseThrow(() -> new IllegalArgumentException("대출 기록이 없거나 이미 반납되었습니다."));

        LocalDateTime now = LocalDateTime.now(); //[추가] 현재 시간 기준
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus("RETURNED");

        // 🔥 [추가] 반납 시점의 최종 연체 일수 계산 로직
        if (now.isAfter(loan.getDueDate())) {
            long days = ChronoUnit.DAYS.between(loan.getDueDate(), now);
            loan.setOverdueDays((int) days);
        } else {
            loan.setOverdueDays(0);
        }
        
        // 리뷰/별점 처리
        if (rating != null) {
            loan.setRating(rating);
        }
        if (reviewContent != null && !reviewContent.trim().isEmpty()) {
            loan.setReviewContent(reviewContent);
        }

        book.setStatus("AVAILABLE");

        // 유저 권수 차감
        UserEntity user = loan.getUser();
        if (user != null && user.getCurrentRentalCount() > 0) {
            user.setCurrentRentalCount(user.getCurrentRentalCount() - 1);
        }

        // 평균 별점 갱신
        updateBookAverageRating(book);

        return "[" + book.getTitle() + "] 반납 완료!";
    }

    /**
     * 3. 특정 도서의 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getBookReviews(Integer bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다."));

        return loanRepository.findByBookAndStatusAndReviewContentIsNotNull(book, "RETURNED")
                .stream()
                .map(loan -> new ReviewResponseDto(
                        loan.getUser().getNickname(),
                        loan.getRating(),
                        loan.getReviewContent(),
                        loan.getReturnDate()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 4. [유저용] 나의 현재 대출 현황 (미반납)
     */
//    @Transactional(readOnly = true)
//    public List<LoanEntity> getMyCurrentLoans(String userId) {
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//        return loanRepository.findByUserAndStatus(user, "BORROWED");
//    }
    
    //[전체 수정]
    @Transactional // [수정] Dirty Checking을 통한 실시간 DB 반영을 위해 readOnly 제거 권장
    public List<LoanResponseDto> getMyCurrentLoans(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        //[수정] 대출 중이거나 연체 중인 모든 정보를 가져옴
        List<LoanEntity> loans = loanRepository.findByUserAndStatusIn(user, List.of("BORROWED", "OVERDUE"));

        //[추가] 엔티티 리스트를 DTO 리스트로 변환하며 실시간 연체 계산
        return loans.stream().map(loan -> {
            if (LocalDateTime.now().isAfter(loan.getDueDate())) {
                long days = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());
                loan.setOverdueDays((int) days);
                loan.setStatus("OVERDUE"); //[추가] 기간 지났으면 상태를 연체로 변경
            }
            return LoanResponseDto.fromEntity(loan);
        }).collect(Collectors.toList());
    }

    /**
     * 5. [유저용] 나의 전체 대출 이력 (History)
     */
    @Transactional(readOnly = true)
    public List<LoanEntity> getMyTotalHistory(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // Repository 메서드명 주의: findByUserOrderByLoanDateDesc
        return loanRepository.findByUserOrderByLoanDateDesc(user);
    }

    /**
     * 6. [관리자용] 특정 유저의 전체 대출 이력 조회
     */
    @Transactional(readOnly = true)
    public List<LoanEntity> getUserHistoryForAdmin(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("조회할 사용자가 없습니다."));
        return loanRepository.findByUserOrderByLoanDateDesc(user);
    }

    /**
     * [내부 로직] 평균 별점 업데이트
     */
    private void updateBookAverageRating(BookEntity book) {
        List<LoanEntity> loans = loanRepository.findByBookAndStatusAndRatingIsNotNull(book, "RETURNED");
        if (!loans.isEmpty()) {
            double average = loans.stream().mapToDouble(LoanEntity::getRating).average().orElse(0.0);
            book.setAvgRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        } else {
            book.setAvgRating(BigDecimal.ZERO.setScale(2));
        }
    }
}