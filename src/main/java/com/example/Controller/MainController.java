package com.example.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.Entity.BookEntity;
import com.example.Entity.UserEntity;
import com.example.Repository.BookRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    // 1. 메인 페이지 (index.html) 연결
    @GetMapping("/")
    public String index() {
    	
        return "index";
    }

    // 2. 마이페이지 (mypage.html) 연결
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
    	UserEntity user = new UserEntity(); // 빈 상자를 먼저 만듭니다. (@NoArgsConstructor 덕분에 가능)
    	user.setUserId("test_id");          // 아이디 넣기
    	user.setNickname("테스터");         // 닉네임 넣기
    	user.setEmail("test@test.com");     // 이메일 넣기
    	user.setPassword("1234");           // 패스워드 넣기

    	model.addAttribute("user", user);
    	model.addAttribute("rentedBooks", new ArrayList<>());
    	model.addAttribute("allRentalHistory", new ArrayList<>());
    	
        return "mypage"; 
    }

    @GetMapping("/return")
    public String returnPage() {
        return "return";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/";
    }
    
    
}
