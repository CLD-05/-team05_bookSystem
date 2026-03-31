package com.example.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LookUpDto {
    private String userId;      // 비밀번호 재설정 시 필요 (누구 비번을 바꿀지)
    private String email;       // 아이디 찾기, 비번 재설정 공통
    private String nickname;    // 아이디 찾기 시 필요
    private String newPassword; // 비밀번호 재설정 시 새로 쓸 비밀번호
}