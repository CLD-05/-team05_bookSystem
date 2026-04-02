package com.example.Dto;

import java.time.LocalDateTime;

import com.example.Entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class LookUpDto {
    private String userId;      // 비밀번호 재설정 시 필요 (누구 비번을 바꿀지)
    private String email;       // 아이디 찾기, 비번 재설정 공통
    private String nickname;    // 아이디 찾기 시 필요
    private String newPassword; // 비밀번호 재설정 시 새로 쓸 비밀번호
    private String role;        // 추가 필요
    private LocalDateTime createdAt; // 추가 필요
    
    public static LookUpDto fromEntity(UserEntity entity) {
        return LookUpDto.builder()
                .userId(String.valueOf(entity.getUserId())) 
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}