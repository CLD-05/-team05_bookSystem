package com.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // 기본 생성자 (JSON 변환용)
@AllArgsConstructor // 모든 필드를 포함한 생성자 (자동 생성됨)
public class ReviewResponseDto {
    private String nickname;
    private Double rating;
    private String content;
    private LocalDateTime date;
}