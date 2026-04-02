package com.example.Repository;

import com.example.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    // 1. 제목 검색 (정상 도서만)
    // 규칙: StatusNot이 먼저 나오고 그 뒤에 TitleContaining이 붙어야 안전합니다.
    List<BookEntity> findByStatusNotAndTitleContaining(String status, String title);

    // 2. 저자 검색 (정상 도서만)
    List<BookEntity> findByStatusNotAndAuthorContaining(String status, String author);

    // 3. 인기 도서 TOP 5 (정상 도서 중에서 렌탈 횟수와 평점 순으로)
    // 메서드명이 길지만 JPA 규칙에 맞춘 이름입니다.
    List<BookEntity> findTop5ByStatusNotOrderByRentalHitCountDescAvgRatingDesc(String status);

    // 4. 전체 도서 목록 (삭제된 도서 제외)
    // 메서드 이름으로 만들기 복잡할 땐 이렇게 @Query를 쓰는 게 훨씬 깔끔합니다.
    @Query("SELECT b FROM BookEntity b WHERE b.status != 'DELETED'")
    List<BookEntity> findAllActiveBooks();
    
    List<BookEntity> findByTitleContainingAndAuthorContaining(String title, String author);

}