package com.example.Controller;

import com.example.Dto.LoginDto;
import com.example.Dto.LookUpDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto dto) {
        userService.signUp(dto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 2. 로그인 (수정본)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto, HttpSession session) {
        UserEntity user = userService.login(dto);

        // 핵심: 세션에 로그인한 유저의 식별자(ID)를 저장합니다.
        // 나중에 대출할 때 이 "loggedInUser" 키로 아이디를 꺼내 쓸 거예요.
        session.setAttribute("loggedInUser", user.getUserId());

        return ResponseEntity.ok(user.getNickname() + "님, 환영합니다! (세션 저장 완료)");
    }

    // 3. 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody LookUpDto dto) {
        String userId = userService.findId(dto);
        return ResponseEntity.ok("찾으시는 아이디는 [" + userId + "] 입니다.");
    }

    // 4. 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody LookUpDto dto) {
        userService.resetPassword(dto);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
    // 5. 로그아웃 (추가 권장)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화 (기억 삭제)
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}