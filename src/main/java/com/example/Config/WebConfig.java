package com.example.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. 이미지 저장 경로 설정 (프로젝트 루트의 uploads 폴더)
    private final String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toString();

    /**
     * [기능 1] CORS 설정
     * 프런트엔드(React 등)에서 백엔드 API에 접근할 수 있게 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 프런트엔드 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * [기능 2] 정적 리소스 핸들러 설정
     * 웹 브라우저에서 /uploads/파일명.jpg 로 접근했을 때 실제 폴더의 파일을 보여줌
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}