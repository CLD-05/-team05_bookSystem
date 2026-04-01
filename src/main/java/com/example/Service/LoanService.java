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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. (ID: " + bookId + ")"));

        if ("RENTED".equals(book.getStatus())) {
            return "이미 다른 사용자가 대여 중인 도서입니다.";
        }

        LoanEntity loan = new LoanEntity();
        loan.setUser(user);
        loan.setBook(book);
        loan.setDueDate(LocalDateTime.now().plusDays(14)); // 14일 대출 기한
        loan.setStatus("BORROWED");

        loanRepository.save(loan);
        book.setStatus("RENTED");
        book.setRentalHitCount(book.getRentalHitCount() + 1);

        return "[" + book.getTitle() + "] 대출 완료! \n" +
                "반납 기한: " + loan.getDueDate();
    }

    /**
     * 2. 도서 반납 및 리뷰 등록 (평균 별점 업데이트 포함)
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

        book.setStatus("AVAILABLE");

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

        // 리뷰가 작성된 반납 기록들 조회
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
     * [내부 로직] 해당 도서의 모든 평점을 계산하여 BookEntity의 avgRating 업데이트
     */
    private void updateBookAverageRating(BookEntity book) {
        List<LoanEntity> loans = loanRepository.findByBookAndStatusAndRatingIsNotNull(book, "RETURNED");

        if (!loans.isEmpty()) {
            double sum = loans.stream()
                    .mapToDouble(LoanEntity::getRating)
                    .sum();
            double average = sum / loans.size();

            // double -> BigDecimal 변환 및 소수점 2자리 반올림
            BigDecimal bdAverage = BigDecimal.valueOf(average)
                    .setScale(2, RoundingMode.HALF_UP);

            book.setAvgRating(bdAverage);
        } else {
            book.setAvgRating(BigDecimal.ZERO.setScale(2));
        }
    }
    @Transactional(readOnly = true)
    public List<LoanEntity> getAllMyLoanHistory(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 상태 상관없이 이 유저의 모든 대출 기록을 최신순으로 가져오기
        return loanRepository.findByUserOrderByLoanDateDesc(user);
    }

}