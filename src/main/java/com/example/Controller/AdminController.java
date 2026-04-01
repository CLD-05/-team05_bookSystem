package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Service.AdminService;
import com.example.Service.BookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;
    private final AdminService adminService;

    // 권한 체크 공통 메소드
    private boolean isNotAdmin(HttpSession session) {
        return !"ADMIN".equals(session.getAttribute("userRole"));
    }

    // 도서 등록
    @PostMapping("/books/register")
    public ResponseEntity<String> registerBook(@RequestBody BookEntity bookEntity, HttpSession session) {
        if (isNotAdmin(session)) return ResponseEntity.status(403).body("관리자 권한이 없습니다.");

        bookService.registerBook(bookEntity);
        return ResponseEntity.ok("성공: [" + bookEntity.getTitle() + "] 도서가 등록되었습니다.");
    }

    // 도서 삭제
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer bookId, HttpSession session) {
        if (isNotAdmin(session)) return ResponseEntity.status(403).body("관리자 권한이 없습니다.");

        try {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok("성공: 도서가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 전체 대출 이력 조회
    @GetMapping("/loans/all")
    public ResponseEntity<List<LoanEntity>> getAllLoans(HttpSession session) {
        if (isNotAdmin(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getAllLoanHistory());
    }

    // 현재 대출 중인 현황 조회
    @GetMapping("/loans/current")
    public ResponseEntity<List<LoanEntity>> getCurrentLoans(HttpSession session) {
        if (isNotAdmin(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(adminService.getCurrentLoans());
    }
}