package com.example.Controller;

import com.example.Dto.LoginDto;
import com.example.Dto.LookUpDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto) {
        UserEntity user = userService.login(dto);
        // 로그인 성공 시 닉네임을 반환하거나 "성공" 메시지를 보냅니다.
        return ResponseEntity.ok(user.getNickname() + "님, 환영합니다!");
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
}