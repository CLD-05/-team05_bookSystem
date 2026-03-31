package com.example.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDto {
    private String nickname; // email -> nickname으로 변경
    private String password;
}