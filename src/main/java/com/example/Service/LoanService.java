package com.example.Service;

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

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 도서 대출 실행
     */
    @Transactional
    public String borrowBook(String userId, Integer bookId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. (ID: " + bookId + ")"));

        if ("RENTED".equals(book.getStatus())) {
            return "이미 다른 사용자가 대여 중인 도서입니다.";
        }

        LoanEntity loan = new LoanEntity();
        loan.setUser(user);
        loan.setBook(book);
        loan.setDueDate(LocalDateTime.now().plusDays(14));
        loan.setStatus("BORROWED");

        loanRepository.save(loan);
        book.setStatus("RENTED");
        book.setRentalHitCount(book.getRentalHitCount() + 1);

        return "[" + book.getTitle() + "] 대출 완료! \n" +
                "반납 기한: " + loan.getDueDate();
    }

    /**
     * 도서 반납 및 리뷰 등록 (평균 별점 업데이트 포함)
     */
    @Transactional
    public String returnBook(Integer bookId, Double rating, String reviewContent) {
        // 1. 도서 및 대출 기록 확인
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없습니다."));

        LoanEntity loan = loanRepository.findByBookAndStatus(book, "BORROWED")
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없거나 이미 반납되었습니다."));

        // 2. 반납 정보 및 리뷰 업데이트
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus("RETURNED");

        if (rating != null) {
            if (rating < 0 || rating > 5 || (rating * 10) % 5 != 0) {
                throw new IllegalArgumentException("별점은 0~5 사이, 0.5 단위여야 합니다.");
            }
            loan.setRating(rating);
        }

        if (reviewContent != null && !reviewContent.trim().isEmpty()) {
            if (reviewContent.length() > 50) {
                throw new IllegalArgumentException("리뷰는 최대 50자까지 가능합니다.");
            }
            loan.setReviewContent(reviewContent);
        }

        // 3. 도서 상태 복구
        book.setStatus("AVAILABLE");

        // 4. 핵심: 책의 평균 별점(avgRating) 갱신 로직
        updateBookAverageRating(book);

        return "[" + book.getTitle() + "] 반납 및 리뷰 등록이 완료되었습니다!";
    }

    /**
     * 해당 도서의 모든 평점을 계산하여 BookEntity의 avgRating을 업데이트
     */
    private void updateBookAverageRating(BookEntity book) {
        // 1. 평점이 있는 반납된 대출 기록들 가져오기
        List<LoanEntity> loans = loanRepository.findByBookAndStatusAndRatingIsNotNull(book, "RETURNED");

        if (!loans.isEmpty()) {
            // 2. 평점 합산 (Double로 계산)
            double sum = loans.stream()
                    .mapToDouble(LoanEntity::getRating)
                    .sum();
            double average = sum / loans.size();

            // 3. 핵심: double을 BigDecimal로 변환하여 저장 (소수점 둘째 자리에서 반올림)
            BigDecimal bdAverage = BigDecimal.valueOf(average)
                    .setScale(2, RoundingMode.HALF_UP);

            book.setAvgRating(bdAverage); // 이제 타입이 일치합니다!
        } else {
            // 평점이 없으면 0.00으로 세팅
            book.setAvgRating(BigDecimal.ZERO.setScale(2));
        }
    }
}