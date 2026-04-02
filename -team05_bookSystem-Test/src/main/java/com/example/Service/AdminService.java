package com.example.Service;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Repository.BookRepository;
import com.example.Repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    /**
     * [관리자] 전체 대출 이력 조회
     */
    @Transactional(readOnly = true)
    public List<LoanEntity> getAllLoanHistory() {
        return loanRepository.findAllByOrderByLoanDateDesc();
    }

    /**
     * [관리자] 현재 대출 중인 현황 조회
     */
    @Transactional(readOnly = true)
    public List<LoanEntity> getCurrentLoans() {
        return loanRepository.findByStatusOrderByDueDateAsc("BORROWED");
    }

    /**
     * [관리자] 신규 도서 등록
     */
    @Transactional
    public void addBook(BookEntity book) {
        book.setStatus("AVAILABLE"); // 초기 상태: 대출 가능
        book.setRentalHitCount(0);   // 대여 횟수 0 초기화
        book.setAvgRating(BigDecimal.valueOf(0.0));      // 평점 0 초기화
        bookRepository.save(book);
    }

    /**
     * [관리자] 도서 삭제 (상태 변경 방식)
     */
    @Transactional
    public void deleteBook(Integer bookId) {
        // 1. DB에서 해당 책을 찾음
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 책이 없습니다."));

        // 2. 대여 중인지 먼저 체크 (대여 중이면 삭제/폐기 불가)
        if ("RENTED".equals(book.getStatus())) {
            throw new IllegalStateException("현재 대여 중인 도서는 삭제할 수 없습니다.");
        }

        // 3. [수정된 부분] 실제 DELETE 대신 상태값만 'DELETED'로 변경
        // 이렇게 하면 외래 키(Foreign Key) 에러 없이 대출 이력을 보존할 수 있습니다.
        book.setStatus("DELETED");

        // 4. 변경된 상태를 저장 (Dirty Checking 덕분에 생략 가능하지만 명시적으로 작성)
        bookRepository.save(book);
    }
}