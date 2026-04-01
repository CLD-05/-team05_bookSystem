package com.example.Repository;

import com.example.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    // 1. 제목으로 검색 (키워드 포함)
    List<BookEntity> findByTitleContaining(String title);

    // 2. 저자로 검색 (키워드 포함)
    List<BookEntity> findByAuthorContaining(String author);

    // 3. 인기도서
    List<BookEntity> findTop5ByOrderByRentalHitCountDescAvgRatingDesc();

    // 4. 삭제된 도서 예외처리
    @Query("SELECT b FROM BookEntity b WHERE b.status != 'DELETED'")
    List<BookEntity> findAllActiveBooks();

    // 제목 검색 시에도 삭제된 건 제외
    List<BookEntity> findByTitleContainingAndStatusNot(String title, String status);
}