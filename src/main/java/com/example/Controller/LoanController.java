package com.example.Controller;

import com.example.Service.LoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // 1. 도서 대출 API
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable Integer bookId, HttpSession session) {
        // 세션에서 로그인한 유저 ID 꺼내기
        String userId = (String) session.getAttribute("loggedInUser");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요한 서비스입니다.");
        }

        try {
            String result = loanService.borrowBook(userId, bookId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } // borrowBook 메서드 끝

    // 2. 도서 반납 API
    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(
            @PathVariable Integer bookId,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String reviewContent) {
        try {
            String result = loanService.returnBook(bookId, rating, reviewContent);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }// returnBook 메서드 끝
}