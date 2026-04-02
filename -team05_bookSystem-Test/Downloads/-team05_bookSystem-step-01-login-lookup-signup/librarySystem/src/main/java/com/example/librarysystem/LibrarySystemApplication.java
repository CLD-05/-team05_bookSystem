package com.example.librarysystem; // 본인의 패키지 경로

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example") // ✅ 이 줄을 추가하세요!
public class LibrarySystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemApplication.class, args);
    }
}