package com.example.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/db-test")
    public String checkDbConnection(Model model) {
        try {
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            model.addAttribute("dbStatus", "✅ 성공 (" + dbName + ")");
        } catch (Exception e) {
            model.addAttribute("dbStatus", "❌ 실패: " + e.getMessage());
        }
        return "admin_index"; // 관리자 페이지 상단이나 하단에 상태를 표시하도록 함
    }
}