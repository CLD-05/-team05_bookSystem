package com.example.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Dto.UserResponseDto;
import com.example.Dto.LoginDto;
import com.example.Dto.LookUpDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 주입
    
    // 1. 회원가입
    @Transactional
    public LookUpDto signUp(SignUpDto dto) { //암호화를 위해 viod를 LookUpDto로 수정
        UserEntity user = new UserEntity();
        user.setUserId(dto.getUserId());
        //user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        user.setRole("USER"); // 기본 권한 설정
        //userRepository.save(user);
        
     // 암호화로 위에 주석처리 된 부분 코드수정
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);

        UserEntity savedUser = userRepository.save(user);
        return LookUpDto.fromEntity(savedUser);
    }

    // 2. 로그인 (어드민/유저 공용)
//    public UserEntity login(LoginDto dto) {
//        return userRepository.findByUserId(dto.getUserId())
//                .filter(u -> u.getPassword().equals(dto.getPassword()))
//                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 틀립니다."));
//    }
    
    
    //암호화로 위에 2. 로그인 코드 전체 수정
    public UserEntity login(LoginDto dto) {
    	// 1. 아이디로 유저를 찾음
        UserEntity user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // 2. matches(평문 비번, DB 암호문)를 사용
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
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

//        // 일반 유저만 비번 변경 진행
//        user.setPassword(dto.getNewPassword());
//        userRepository.save(user);
//   }
        // 암호화로 위에 일반 유저만 비번 변경 진행부분 코드 수정
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedNewPassword);
    
        userRepository.save(user);
    }
    
    /**
     * 5. 전체 사용자 목록 조회 (관리자용)
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        // DB에서 모든 유저 엔티티를 가져와서 DTO 리스트로 변환합니다.
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
}