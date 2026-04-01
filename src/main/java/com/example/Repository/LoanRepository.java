package com.example.Repository;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, Integer> {
    // bookId 숫자 대신 BookEntity 객체를 넣어 조회합니다.
    Optional<LoanEntity> findByBookAndStatus(BookEntity book, String status);

    List<LoanEntity> findByBookAndStatusAndRatingIsNotNull(BookEntity book, String status);

    List<LoanEntity> findByBookAndStatusAndReviewContentIsNotNull(BookEntity book, String status);

    List<LoanEntity> findByUserAndStatus(UserEntity user, String status);

    List<LoanEntity> findByUserOrderByLoanDateDesc(UserEntity user);
    // 1. 전체 대출 이력 (최신순)
    List<LoanEntity> findAllByOrderByLoanDateDesc();

    // 2. 현재 대출 중인 것만 (상태가 BORROWED인 것)
    List<LoanEntity> findByStatusOrderByDueDateAsc(String status);
}