package com.example.Controller;

import com.example.Dto.LoginDto;
import com.example.Dto.LookUpDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller; // @RestController에서 변경
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user") // /api/users 대신 일반 경로 사용
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // [페이지] 로그인 화면
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html
    }

    // [페이지] 회원가입 화면
    @GetMapping("/signup")
    public String signupPage() {
        return "sign_up"; // sign_up.html
    }

    // 1. 회원가입 처리 (액션)
    @PostMapping("/signup")
    public String signUp(@ModelAttribute SignUpDto dto, Model model) {
        try {
            userService.signUp(dto);
            return "sign_up_result"; // sign_up_result.html (가입완료 알림 페이지)
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "sign_up";
        }
    }

    // 2. 로그인 처리 (세션 저장 및 리다이렉트)
    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto dto, HttpSession session, Model model) {
        try {
            UserEntity user = userService.login(dto);

            // 세션 저장: 로그인 유저 ID와 권한(ROLE)
            session.setAttribute("loggedInUser", user.getUserId());
            session.setAttribute("userRole", user.getRole());

            // 권한에 따른 초기 페이지 분기
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/index";
            }
            return "redirect:/books/index";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login"; // 실패 시 메시지와 함께 로그인창 유지
        }
    }

    // [페이지] 아이디 찾기 화면
    @GetMapping("/find-id")
    public String findIdPage() {
        return "id_search"; // id_search.html
    }

    // 3. 아이디 찾기 처리
    @PostMapping("/find-id")
    public String findId(@ModelAttribute LookUpDto dto, Model model) {
        try {
            String userId = userService.findId(dto);
            model.addAttribute("foundId", userId);
            model.addAttribute("userName", dto.getNickname());
            return "id_search_result"; // id_search_result.html
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "id_search";
        }
    }

    // [페이지] 비밀번호 재설정 화면
    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "pw_reset1"; // pw_reset1.html
    }

    // 4. 비밀번호 재설정 처리
    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute LookUpDto dto, Model model) {
        try {
            userService.resetPassword(dto);
            return "pw_reset_result"; // pw_reset_result.html
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pw_reset1";
        }
    }

    // 5. 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }
}