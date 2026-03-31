package com.example.Service;

import com.example.Dto.LoginDto;
import com.example.Dto.LookUpDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 1. 회원가입
    @Transactional
    public void signUp(SignUpDto dto) {
        UserEntity user = new UserEntity();
        user.setUserId(dto.getUserId());
        user.setPassword(dto.getPassword()); // 실제론 암호화(BCrypt) 권장
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        userRepository.save(user);
    }

    // 2. 로그인 (아이디 + 비번 일치 확인)
    public UserEntity login(LoginDto dto) {
        return userRepository.findByUserId(dto.getUserId())
                .filter(u -> u.getPassword().equals(dto.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 틀립니다."));
    }

    // 아이디 찾기: 이메일과 닉네임이 모두 일치하는 유저의 ID를 반환
    public String findId(LookUpDto dto) {
        return userRepository.findByEmailAndNickname(dto.getEmail(), dto.getNickname())
                .map(UserEntity::getUserId) // 찾았다면 userId만 꺼냄
                .orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 아이디가 없습니다."));
    }

    // 비밀번호 재설정: 이메일과 아이디가 일치하는 유저를 찾아 비번 변경
    @Transactional
    public void resetPassword(LookUpDto dto) {
        UserEntity user = userRepository.findByEmailAndUserId(dto.getEmail(), dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 이메일 정보가 잘못되었습니다."));

        // 새 비밀번호로 업데이트
        user.setPassword(dto.getNewPassword());
    }
}