package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminController { // 클래스 이름을 AdminController로 명확히!

    private final BookService bookService;

    @PostMapping("/register")
    public ResponseEntity<String> registerBook(@RequestBody BookEntity bookEntity) {
        BookEntity savedBook = bookService.registerBook(bookEntity);
        return ResponseEntity.ok("성공: [" + savedBook.getTitle() + "] 도서가 등록되었습니다.");
    }
}