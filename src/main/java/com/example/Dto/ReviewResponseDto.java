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

    // 타임리프에서 review.starString 으로 접근 가능하도록 추가
    public String getStarString() {
        if (rating == null) return "☆☆☆☆☆";
        int starCount = (int) Math.round(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < starCount) sb.append("★");
            else sb.append("☆");
        }
        return sb.toString();
    }
}