package com.example.Controller;

import com.example.Dto.SignUpDto;
import com.example.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor // @Autowired 대신 생성자 주입을 사용하는 시니어 스타일!
public class ViewController {

    private final UserService userService;

    // 1. 회원가입 페이지 화면 띄우기
    @GetMapping("/signup")
    public String signUpPage() {
        return "sign_up"; 
    }

    // 2. 가입 결과 페이지 화면 띄우기
    @GetMapping("/sign_up_result")
    public String signUpResultPage() {
        return "sign_up_result"; 
    }

    // 3. 실제 회원가입 데이터 처리 (POST)
    @PostMapping("/signup")
    public String registerUser(SignUpDto signUpDto) {
        // [중요] HTML에서 분리해서 보낸 email1, email2를 합쳐서 DTO의 email 필드에 세팅
        if (signUpDto.getEmail1() != null && signUpDto.getEmail2() != null) {
            signUpDto.setEmail(signUpDto.getEmail1() + "@" + signUpDto.getEmail2());
        }

        // 서비스의 signUp 메소드 호출 (DB 인서트 실행)
        userService.signUp(signUpDto);
        
        System.out.println("회원가입 완료! 아이디: " + signUpDto.getUserId());
        
        // 가입 성공 후 결과 페이지로 리다이렉트
        return "redirect:/sign_up_result"; 
    }

    @GetMapping("/admin/index")
    public String adminIndex() {
        return "admin_index"; 
    }

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin_login"; 
    }
}