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
    
    
    // 🔥 [추가] 여러 상태값(BORROWED, OVERDUE)을 리스트로 받아서 도서로 대출 기록 찾기
    // 반납할 때 '대출중'이거나 '연체중'인 기록을 모두 찾아야 에러가 안 납니다.
    Optional<LoanEntity> findByBookAndStatusIn(BookEntity book, List<String> statuses);

    // 🔥 [추가] 유저의 현재 대출/연체 현황을 한꺼번에 가져오기
    // '나의 대출 현황' 조회 시 실시간 연체 갱신을 위해 필요합니다.
    List<LoanEntity> findByUserAndStatusIn(UserEntity user, List<String> statuses);
}