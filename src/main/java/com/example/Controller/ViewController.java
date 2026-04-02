package com.example.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // RestController가 아님에 주의!
public class ViewController {

    @GetMapping("/admin/index")
    public String adminIndex() {
        return "admin_index"; // templates/admin_main.html 파일을 열어줌
    }

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin_login"; // templates/book_register.html 파일을 열어줌
    }
}