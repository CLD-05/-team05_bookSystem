package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Entity.UserEntity;
import com.example.Service.LoanService;
import com.example.Repository.BookRepository;
import com.example.Repository.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 1. 도서 대출 신청 (AJAX/Fetch 대응을 위해 JSON 대신 리다이렉트 또는 메시지 반환 가능)
     * 여기서는 @ResponseBody를 붙여 JSON으로 성공 여부를 반환하도록 수정합니다.
     */
    @PostMapping("/borrow/{bookId}")
    @ResponseBody
    public java.util.Map<String, Object> borrowBook(@PathVariable Integer bookId, HttpSession session) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        String userId = (String) session.getAttribute("loggedInUser");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            String resultMessage = loanService.borrowBook(userId, bookId);
            if (resultMessage.contains("완료")) {
                response.put("success", true);
                response.put("message", resultMessage);
            } else {
                response.put("success", false);
                response.put("message", resultMessage);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }
        return response;
    }

    /**
     * 2. 도서 반납 처리 (AJAX/Fetch 대응)
     * rating과 reviewContent가 같이 오면 바로 처리, 아니면 반납만 처리
     */
    @PostMapping("/return/{bookId}")
    @ResponseBody
    public java.util.Map<String, Object> returnBook(@PathVariable Integer bookId,
                                                   @RequestParam(required = false) Double rating,
                                                   @RequestParam(required = false) String reviewContent) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            // reviewContent 파라미터명이 HTML form의 'comment'와 다를 수 있으므로 
            // 호출하는 쪽에서 맞추거나 여기서 둘 다 확인하도록 유연하게 처리 가능
            String resultMessage = loanService.returnBook(bookId, rating, reviewContent);
            response.put("success", true);
            response.put("message", resultMessage);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "반납 중 오류가 발생했습니다: " + e.getMessage());
        }
        return response;
    }

    /**
     * 2-1. 반납 후 리뷰만 따로 저장하는 기능 (return.html 대응)
     */
    @PostMapping("/review/save")
    public String saveReview(@RequestParam Integer bookId,
                             @RequestParam(required = false) Double rating,
                             @RequestParam(required = false) String comment,
                             HttpSession session) {
        try {
            loanService.saveReview(bookId, rating, comment);
            return "redirect:/loans/my-loans";
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

        // 1. 사용자 정보 가져오기 (닉네임, 이메일, 남은 권수 등)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 현재 대출 목록 (BORROWED 상태)
        List<LoanEntity> currentLoans = loanService.getMyCurrentLoans(userId);

        // 3. 전체 대출 이력 (History)
        List<LoanEntity> history = loanService.getMyTotalHistory(userId);

        model.addAttribute("user", user);
        model.addAttribute("loans", currentLoans);
        model.addAttribute("history", history);

        return "mypage";
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