package com.example.Controller;

import com.example.Dto.LoginDto;
import com.example.Dto.LookUpDto;
import com.example.Dto.SignUpDto;
import com.example.Dto.UserResponseDto;
import com.example.Entity.UserEntity;
import com.example.Service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    // 2. 로그인 (세션에 ID와 ROLE 모두 저장)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto, HttpSession session) {
        try {
            UserEntity user = userService.login(dto);

            // 세션 저장: 나중에 대출이나 어드민 권한 체크 시 사용
            session.setAttribute("loggedInUser", user.getUserId());
            session.setAttribute("userRole", user.getRole());

            return ResponseEntity.ok(user.getNickname() + "님, 환영합니다! (권한: " + user.getRole() + ")");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 3. 아이디 찾기 (Service에서 일반유저만 찾도록 로직 구현됨)
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody LookUpDto dto) {
        try {
            String userId = userService.findId(dto);
            return ResponseEntity.ok("찾으시는 아이디는 [" + userId + "] 입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 4. 비밀번호 재설정 (어드민 차단 예외 처리)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody LookUpDto dto) {
        try {
            userService.resetPassword(dto);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            // 어드민 계정일 경우 여기서 "관리자 계정은 직접 재설정 불가..." 메시지가 나감
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // 5. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
    
    /**
     * 6. 전체 사용자 목록 조회 (관리자 전용)
     * GET /api/users/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        // 1. 세션에서 현재 로그인한 유저의 권한(Role)을 가져옵니다.
        String role = (String) session.getAttribute("userRole");

        // 2. 권한이 없거나 "ADMIN"이 아니면 접근을 차단합니다 (403 Forbidden).
        if (role == null || !"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("관리자 권한이 필요한 페이지입니다.");
        }

        // 3. 서비스에서 DTO 리스트를 받아와서 반환합니다.
        List<UserResponseDto> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }
}