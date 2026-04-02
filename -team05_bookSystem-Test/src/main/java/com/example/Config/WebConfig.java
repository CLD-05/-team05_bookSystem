package com.example.Config; // 본인의 패키지 경로

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	// 1. [추가] application.properties에 설정한 물리 경로를 가져옴
    @Value("${file.upload-dir}")
    private String uploadDir;
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로에 대해
                .allowedOrigins("http://localhost:3000") // 프런트엔드 서버 주소 (React 기본값)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    // 2. [신규 추가] 정적 리소스(이미지) 경로 매핑 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * addResourceHandler("/images/**"): 
         * 브라우저에서 http://localhost:8080/images/파일명.jpg 로 접근하면
         * * addResourceLocations("file:///" + uploadDir):
         * 실제 서버 컴퓨터의 uploadDir(C:/CE/.../static/images/) 폴더에서 파일을 찾음
         */
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///" + uploadDir);
    }
}