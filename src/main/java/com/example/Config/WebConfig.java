package com.example.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. 경로를 /app/images/ 로 직접 고정 (도커 내부 경로)
    // 리눅스 환경이므로 System.getProperty("user.dir") 대신 절대 경로를 쓰는 게 안전합니다.
    private final String uploadPath = "/app/images/";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://118.33.135.146:8080") // 서버 IP도 추가하면 좋습니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 2. DB에 /images/라고 저장되어 있으니, 핸들러도 /images/** 로 변경!
        registry.addResourceHandler("/images/**")
                // 3. 실제 파일이 들어있는 도커 내부의 /app/images/ 폴더를 바라보게 설정
                .addResourceLocations("file:" + uploadPath);
    }
}