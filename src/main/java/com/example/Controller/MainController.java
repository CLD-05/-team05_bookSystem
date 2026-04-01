package com.example.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        return "mypage"; 
    }

    @GetMapping("/return")
    public String returnPage() {
        return "return";
    }
}
