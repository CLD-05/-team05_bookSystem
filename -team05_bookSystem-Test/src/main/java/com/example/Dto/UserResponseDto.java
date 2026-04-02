package com.example.Dto;

import com.example.Entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponseDto {
    private String userId;
    private String email;
    private String nickname;
    private String role;

    // 엔티티를 DTO로 변환해주는 도구
    public static UserResponseDto fromEntity(UserEntity user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole());
        return dto;
    }
}