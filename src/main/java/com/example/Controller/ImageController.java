package com.example.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    // 이미지가 저장될 실제 경로 (예시: 프로젝트 폴더 내 uploads)
    private final String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toString();

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 폴더가 없으면 생성
            File folder = new File(uploadPath);
            if (!folder.exists()) folder.mkdirs();

            // 파일명 중복 방지 (UUID 사용)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadPath, fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 프론트엔드에서 접근 가능한 URL 반환 (예: http://localhost:8080/uploads/파일명)
            return ResponseEntity.ok("/uploads/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 업로드 실패: " + e.getMessage());
        }
    }
}