package com.example.Repository;

import com.example.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    // 기본 CRUD(저장, 삭제, 조회) 기능을 상속받습니다.
}