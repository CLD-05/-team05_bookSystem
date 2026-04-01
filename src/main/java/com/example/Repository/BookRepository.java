package com.example.Repository;

import com.example.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    // 1. 제목으로 검색 (키워드 포함)
    List<BookEntity> findByTitleContaining(String title);

    // 2. 저자로 검색 (키워드 포함)
    List<BookEntity> findByAuthorContaining(String author);
}