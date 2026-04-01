package com.example.Repository;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, Integer> {
    // bookId 숫자 대신 BookEntity 객체를 넣어 조회합니다.
    Optional<LoanEntity> findByBookAndStatus(BookEntity book, String status);
    List<LoanEntity> findByBookAndStatusAndRatingIsNotNull(BookEntity book, String status);
}