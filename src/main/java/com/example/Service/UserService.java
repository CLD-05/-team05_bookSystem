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
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        user.setRole("USER"); // 기본 권한 설정
        userRepository.save(user);
    }

    // 2. 로그인 (어드민/유저 공용)
    public UserEntity login(LoginDto dto) {
        return userRepository.findByUserId(dto.getUserId())
                .filter(u -> u.getPassword().equals(dto.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 틀립니다."));
    }

    // 3. 아이디 찾기 (일반 유저만 가능하도록 필터링)
    public String findId(LookUpDto dto) {
        return userRepository.findByEmailAndNicknameAndRole(dto.getEmail(), dto.getNickname(), "USER")
                .map(UserEntity::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 일반 사용자 정보가 없습니다."));
    }

    // 4. 비밀번호 재설정 (어드민 차단 로직 포함)
    @Transactional
    public void resetPassword(LookUpDto dto) {
        UserEntity user = userRepository.findByEmailAndUserId(dto.getEmail(), dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 이메일 정보가 잘못되었습니다."));

        // 관리자 권한이면 즉시 에러 발생
        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("관리자 계정은 보안상 직접 재설정이 불가합니다. 시스템 관리자에게 문의하세요.");
        }

        // 일반 유저만 비번 변경 진행
        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }
}