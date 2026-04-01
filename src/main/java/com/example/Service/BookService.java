package com.example.Service;

import com.example.Entity.BookEntity;
import com.example.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // 리스트 사용을 위해 추가

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // 1. 관리자: 새 도서 등록
    @Transactional
    public BookEntity registerBook(BookEntity bookEntity) {
        if (bookEntity.getStatus() == null) {
            bookEntity.setStatus("AVAILABLE");
        }
        return bookRepository.save(bookEntity);
    }

    // 2. 유저: 전체 도서 목록 조회 (추가된 부분)
    @Transactional(readOnly = true) // 읽기 전용으로 성능 최적화
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }
    // 제목으로 검색
    @Transactional(readOnly = true)
    public List<BookEntity> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    // 저자로 검색
    @Transactional(readOnly = true)
    public List<BookEntity> searchByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }
}