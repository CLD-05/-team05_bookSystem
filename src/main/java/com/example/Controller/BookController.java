package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books") // 유저용 경로는 /api/books로 잡을게요
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 모든 책 리스트 가져오기
    @GetMapping
    public ResponseEntity<List<BookEntity>> getAllBooks() {
        List<BookEntity> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
}