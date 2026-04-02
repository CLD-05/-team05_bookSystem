package com.example.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller; // @RestController에서 변경
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/admin/images")
@RequiredArgsConstructor
public class ImageController {

    private final String uploadPath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/uploads").toString();

    /**
     * 이미지 업로드 처리 (타임리프 폼 전송 방식)
     */
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            File folder = new File(uploadPath);
            if (!folder.exists()) folder.mkdirs();

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadPath, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 업로드된 파일 경로를 모델에 담아 다시 등록 페이지로 전달 (혹은 세션 이용)
            model.addAttribute("uploadedImageUrl", "/uploads/" + fileName);
            model.addAttribute("message", "이미지가 성공적으로 업로드되었습니다.");

            return "admin_book_add"; // 도서 등록 페이지로 다시 이동
        } catch (IOException e) {
            model.addAttribute("error", "업로드 실패: " + e.getMessage());
            return "admin_book_add";
        }
    }
}