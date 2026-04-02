package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Service.LoanService;
import com.example.Repository.BookRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller; // @RestController에서 변경
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookRepository bookRepository;

    /**
     * 1. 도서 대출 신청 (액션 후 결과 페이지로)
     */
    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Integer bookId, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/user/login";

        try {
            loanService.borrowBook(userId, bookId);
            return "redirect:/loans/my-loans"; // 대출 성공 후 현황 페이지로 이동
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "book_detail"; // 실패 시 상세페이지에서 에러 메시지 표시
        }
    }

    /**
     * 2. 도서 반납 처리 (반납 후 리뷰 작성 페이지 return.html로 이동)
     */
    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable Integer bookId,
                             @RequestParam(required = false) Double rating,
                             @RequestParam(required = false) String reviewContent) {
        try {
            loanService.returnBook(bookId, rating, reviewContent);
            return "redirect:/loans/my-history"; // 반납 완료 후 이력 페이지로 이동
        } catch (Exception e) {
            return "redirect:/loans/my-loans?error=" + e.getMessage();
        }
    }

    /**
     * 3. 나의 대출 현황 (mypage.html 연동)
     */
    @GetMapping("/my-loans")
    public String getMyLoans(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/user/login";

        List<LoanEntity> currentLoans = loanService.getMyCurrentLoans(userId);
        model.addAttribute("loans", currentLoans);
        return "mypage"; // src/main/resources/templates/mypage.html
    }

    /**
     * 4. 나의 전체 대출 이력 조회
     */
    @GetMapping("/my-history")
    public String getMyHistory(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/user/login";

        model.addAttribute("history", loanService.getMyTotalHistory(userId));
        return "my_history_page"; // 필요 시 생성
    }

    /**
     * 5. 관리자용: 특정 유저의 대출 이력 상세 조회 (admin_user.html 연동)
     */
    @GetMapping("/admin/user/{userId}/history")
    public String getUserHistoryForAdmin(@PathVariable String userId, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) return "redirect:/login";

        model.addAttribute("userHistory", loanService.getUserHistoryForAdmin(userId));
        model.addAttribute("targetUserId", userId);
        return "admin_user_detail"; // templates/admin_user_detail.html
    }
}