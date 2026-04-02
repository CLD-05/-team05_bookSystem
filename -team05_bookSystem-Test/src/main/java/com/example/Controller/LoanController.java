package com.example.Controller;

import com.example.Dto.LoanResponseDto; // [추가]
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
import java.util.stream.Collectors; // [추가]

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookRepository bookRepository;

    /**
     * 1. 도서 대출 신청
     */
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable Integer bookId, HttpSession session) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            return ResponseEntity.ok(loanService.borrowBook(userId, bookId));
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
            return ResponseEntity.ok(loanService.returnBook(bookId, rating, reviewContent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [조회 1] 특정 도서의 리뷰 목록 조회
     */
    @GetMapping("/book/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getBookReviews(@PathVariable Integer bookId) {
        return ResponseEntity.ok(loanService.getBookReviews(bookId));
    }

    /**
     * [조회 2] 나의 현재 대출 현황 (미반납 건)
     */
//    @GetMapping("/my-loans")
//    public ResponseEntity<List<LoanEntity>> getMyLoans(HttpSession session) {
//        String userId = (String) session.getAttribute("loggedInUser");
//        if (userId == null) return ResponseEntity.status(401).build();
//
//        return ResponseEntity.ok(loanService.getMyCurrentLoans(userId));
//    }
    
    //[수정] 반환 타입을 List<LoanResponseDto>로 변경
    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(HttpSession session) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return ResponseEntity.status(401).build();

        // 서비스에서 List<LoanResponseDto>를 반환하도록 호출!
        return ResponseEntity.ok(loanService.getMyCurrentLoans(userId));
    }
    
    /**
     * [조회 3] 나의 전체 대출 이력 (반납 완료 포함)
     */
//    @GetMapping("/my-history")
//    public ResponseEntity<List<LoanEntity>> getMyHistory(HttpSession session) {
//        String userId = (String) session.getAttribute("loggedInUser");
//        if (userId == null) return ResponseEntity.status(401).build();
//
//        return ResponseEntity.ok(loanService.getMyTotalHistory(userId));
//    }
    
    // 🔥 [수정] 반환 타입을 List<LoanResponseDto>로 변경하여 연체 정보를 포함시킴
    @GetMapping("/my-history")
    public ResponseEntity<List<LoanResponseDto>> getMyHistory(HttpSession session) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return ResponseEntity.status(401).build();

        // 서비스에서 List<LoanEntity>를 줄 경우 여기서 Dto로 변환합니다.
        List<LoanResponseDto> history = loanService.getMyTotalHistory(userId).stream()
                .map(LoanResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    /**
     * [조회 4] 평점/대여순 상위 5위 도서 (TOP 5)
     */
    @GetMapping("/top5")
    public ResponseEntity<List<BookEntity>> getTop5Books() {
        return ResponseEntity.ok(bookRepository.findTop5ByStatusNotOrderByRentalHitCountDescAvgRatingDesc("DELETED"));
    }

    /**
     * [관리자용] 특정 유저의 대출 이력 상세 조회
     */
//    @GetMapping("/admin/user/{userId}/history")
//    public ResponseEntity<List<LoanEntity>> getUserHistoryForAdmin(@PathVariable String userId) {
//        // 관리자가 유저 관리 페이지에서 특정 유저 클릭 시 호출
//        return ResponseEntity.ok(loanService.getUserHistoryForAdmin(userId));
//    }
    
    // 🔥 [수정] 관리자 페이지에서도 연체 여부를 확인해야 하므로 Dto 변환
    @GetMapping("/admin/user/{userId}/history")
    public ResponseEntity<List<LoanResponseDto>> getUserHistoryForAdmin(@PathVariable String userId) {
        List<LoanResponseDto> history = loanService.getUserHistoryForAdmin(userId).stream()
                .map(LoanResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }
}