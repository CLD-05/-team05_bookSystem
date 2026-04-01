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
     * 대출 시 유저의 currentRentalCount를 1 증가시킵니다.
     */
    @Transactional
    public String borrowBook(String userId, Integer bookId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. (ID: " + bookId + ")"));

        // 대출 가능 여부 체크
        if ("RENTED".equals(book.getStatus())) {
            return "이미 다른 사용자가 대여 중인 도서입니다.";
        }

        if (user.getCurrentRentalCount() >= 3) { // 예시: 인당 3권 제한
            return "대출 가능 수량(3권)을 초과하였습니다.";
        }

        LoanEntity loan = new LoanEntity();
        loan.setUser(user);
        loan.setBook(book);
        loan.setDueDate(LocalDateTime.now().plusDays(14)); // 14일 대출 기한
        loan.setStatus("BORROWED");

        loanRepository.save(loan);

        // 상태 업데이트
        book.setStatus("RENTED");
        book.setRentalHitCount(book.getRentalHitCount() + 1);

        // 유저 대출 권수 증가
        user.setCurrentRentalCount(user.getCurrentRentalCount() + 1);

        return "[" + book.getTitle() + "] 대출 완료! \n" +
                "반납 기한: " + loan.getDueDate();
    }

    /**
     * 2. 도서 반납 및 리뷰 등록 (유저 권수 차감 및 평균 별점 업데이트 포함)
     * 기존 bookId 방식에서 더 정확한 처리를 위해 로직을 보완했습니다.
     */
    @Transactional
    public String returnBook(Integer bookId, Double rating, String reviewContent) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

        LoanEntity loan = loanRepository.findByBookAndStatus(book, "BORROWED")
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없거나 이미 반납되었습니다."));

        // 반납 정보 및 리뷰 업데이트
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus("RETURNED");

        // 별점 검증 (0.5 단위)
        if (rating != null) {
            if (rating < 0 || rating > 5 || (rating * 10) % 5 != 0) {
                throw new IllegalArgumentException("별점은 0~5 사이, 0.5 단위여야 합니다.");
            }
            loan.setRating(rating);
        }

        // 리뷰 글자수 검증
        if (reviewContent != null && !reviewContent.trim().isEmpty()) {
            if (reviewContent.length() > 50) {
                throw new IllegalArgumentException("리뷰는 최대 50자까지 가능합니다.");
            }
            loan.setReviewContent(reviewContent);
        }

        // 도서 상태 복구
        book.setStatus("AVAILABLE");

        // [추가] 유저 대출 권수 차감
        UserEntity user = loan.getUser();
        if (user != null && user.getCurrentRentalCount() > 0) {
            user.setCurrentRentalCount(user.getCurrentRentalCount() - 1);
        }

        // 평균 별점(avgRating) 갱신 로직 실행
        updateBookAverageRating(book);

        return "[" + book.getTitle() + "] 반납 및 리뷰 등록이 완료되었습니다!";
    }

    /**
     * 3. 특정 도서의 리뷰 목록 조회 (DTO 변환)
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getBookReviews(Integer bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다."));

        List<LoanEntity> loans = loanRepository.findByBookAndStatusAndReviewContentIsNotNull(book, "RETURNED");

        return loans.stream()
                .map(loan -> new ReviewResponseDto(
                        loan.getUser().getNickname(),
                        loan.getRating(),
                        loan.getReviewContent(),
                        loan.getReturnDate()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 4. 나의 현재 대출 현황 조회
     */
    @Transactional(readOnly = true)
    public List<LoanEntity> getMyCurrentLoans(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return loanRepository.findByUserAndStatus(user, "BORROWED");
    }

    /**
     * 5. 나의 전체 대출 이력 조회
     */
    @Transactional(readOnly = true)
    public List<LoanEntity> getAllMyLoanHistory(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return loanRepository.findByUserOrderByLoanDateDesc(user);
    }

    /**
     * [내부 로직] 해당 도서의 모든 평점을 계산하여 BookEntity의 avgRating 업데이트
     */
    private void updateBookAverageRating(BookEntity book) {
        List<LoanEntity> loans = loanRepository.findByBookAndStatusAndRatingIsNotNull(book, "RETURNED");

        if (!loans.isEmpty()) {
            double sum = loans.stream()
                    .mapToDouble(LoanEntity::getRating)
                    .sum();
            double average = sum / loans.size();

            BigDecimal bdAverage = BigDecimal.valueOf(average)
                    .setScale(2, RoundingMode.HALF_UP);

            book.setAvgRating(bdAverage);
        } else {
            book.setAvgRating(BigDecimal.ZERO.setScale(2));
        }
    }
}