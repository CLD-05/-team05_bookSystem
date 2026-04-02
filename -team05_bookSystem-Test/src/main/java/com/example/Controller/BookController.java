package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 모든 활성 책 리스트 가져오기 (DELETED 제외)
     * GET /api/books/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<BookEntity>> getAllBooks() {
        // Service에서 새로 만든 getAllActiveBooks() 호출
        List<BookEntity> books = bookService.getAllActiveBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * 제목 검색 API (DELETED 제외)
     * GET /api/books/search/title?keyword=...
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<BookEntity>> searchByTitle(@RequestParam String keyword) {
        // Service 내부에서 이미 "DELETED" 필터링을 하도록 수정했으므로 그대로 호출
        List<BookEntity> books = bookService.searchByTitle(keyword);
        return ResponseEntity.ok(books);
    }

    /**
     * 저자 검색 API (DELETED 제외)
     * GET /api/books/search/author?keyword=...
     */
    @GetMapping("/search/author")
    public ResponseEntity<List<BookEntity>> searchByAuthor(@RequestParam String keyword) {
        List<BookEntity> books = bookService.searchByAuthor(keyword);
        return ResponseEntity.ok(books);
    }
}