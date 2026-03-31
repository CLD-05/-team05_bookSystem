package com.example.Controller;

import com.example.Dto.LoginDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public String signUp(@RequestBody SignUpDto signUpDto) {
        return userService.signUp(signUpDto);
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        UserEntity loginUser = userService.login(loginDto);

        if (loginUser == null) {
            return "닉네임 또는 비밀번호가 틀렸습니다.";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", loginUser);
        return "로그인 성공! " + loginUser.getNickname() + "님 환영합니다.";
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "로그아웃 되었습니다.";
    }

    // 아이디(닉네임) 찾기
    @GetMapping("/find-id")
    public String findId(@RequestParam String email, @RequestParam String password) {
        return userService.findNickname(email, password);
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> params) {
        return userService.resetPassword(
                params.get("nickname"),
                params.get("email"),
                params.get("newPassword")
        );
    }
}