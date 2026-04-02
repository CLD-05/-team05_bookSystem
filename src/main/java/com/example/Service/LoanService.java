package com.example.Service;

import com.example.Dto.ReviewResponseDto;
import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Entity.UserEntity;
import com.example.Repository.BookRepository;
import com.example.Repository.LoanRepository;
import com.example.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        LoanEntity loan = loanRepository.findByBookAndStatus(book, "BORROWED")
                .orElseThrow(() -> new IllegalArgumentException("대출 기록이 없거나 이미 반납되었습니다."));

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus("RETURNED");

        // 리뷰/별점 처리 (반납 시 동시에 할 경우)
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
     * 2-1. 반납 후 리뷰만 별도로 저장
     */
    @Transactional
    public void saveReview(Integer bookId, Double rating, String comment) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

        // 가장 최근의 RETURNED 상태인 대여 기록을 찾음
        // (방금 반납한 기록에 리뷰를 남기는 상황이므로)
        LoanEntity loan = loanRepository.findTopByBookAndStatusOrderByReturnDateDesc(book, "RETURNED")
                .orElseThrow(() -> new IllegalArgumentException("반납된 대출 기록을 찾을 수 없습니다."));

        if (rating != null) {
            loan.setRating(rating);
        }
        if (comment != null && !comment.trim().isEmpty()) {
            loan.setReviewContent(comment);
        }

        // 도서의 평균 별점 다시 계산
        updateBookAverageRating(book);
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
    @Transactional(readOnly = true)
    public List<LoanEntity> getMyCurrentLoans(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return loanRepository.findByUserAndStatus(user, "BORROWED");
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