package com.example.Controller;

import com.example.Dto.ReviewResponseDto;
import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Repository.BookRepository;
import com.example.Service.LoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookRepository bookRepository; // 1. 선언이 빠져있어서 추가했습니다.

    /**
     * 1. 도서 대출 신청
     */
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable Integer bookId, HttpSession session) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            String result = loanService.borrowBook(userId, bookId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 2. 도서 반납 및 리뷰 등록
     */
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
    }

    /**
     * [기능 1] 특정 도서의 리뷰 목록 조회
     */
    @GetMapping("/book/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getBookReviews(@PathVariable Integer bookId) {
        return ResponseEntity.ok(loanService.getBookReviews(bookId));
    }

    /**
     * [기능 2] 나의 현재 대출 현황 조회 (마이페이지용)
     */
    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanEntity>> getMyLoans(HttpSession session) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(loanService.getMyCurrentLoans(userId));
    }

    /**
     * [기능 3] 평점 상위 5위 도서 조회 (TOP 5)
     */
    @GetMapping("/top5")
    public ResponseEntity<List<BookEntity>> getTop5Books() {
        // Repository의 3번 메서드 호출 + "DELETED" 인자 전달
        return ResponseEntity.ok(bookRepository.findTop5ByStatusNotOrderByRentalHitCountDescAvgRatingDesc("DELETED"));
    }
} // 2. 클래스를 닫는 이 중괄호가 없어서 에러가 났던 것입니다!