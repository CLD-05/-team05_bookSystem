package com.example.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/db-test")
    public String checkDbConnection() {
        try {
            // 현재 접속 중인 DB 이름을 가져오는 쿼리 실행
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            return "✅ DB 연결 성공! 현재 데이터베이스: " + dbName;
        } catch (Exception e) {
            return "❌ DB 연결 실패: " + e.getMessage();
        }
    }
}