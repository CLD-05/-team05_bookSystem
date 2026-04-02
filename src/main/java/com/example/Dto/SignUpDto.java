package com.example.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString // 로그 찍을 때 데이터 확인하기 편하게 추가했습니다!
public class SignUpDto {
    // 1. 실제 DB 저장용 필드
    private String email;    
    private String userId;
    private String password;
    private String nickname;

    // 2. HTML 화면 수신용 필드 (추가 필수!)
    // 화면의 <input name="email1"> 과 <input name="email2"> 값을 여기 담습니다.
    private String email1;
    private String email2;
}