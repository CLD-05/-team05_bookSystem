package com.example.Service;

import com.example.Dto.LoginDto;
import com.example.Dto.SignUpDto;
import com.example.Entity.UserEntity;
import com.example.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 1. 회원가입
    public String signUp(SignUpDto signUpDto) {
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            return "이미 가입된 이메일입니다.";
        }
        if (userRepository.findByNickname(signUpDto.getNickname()).isPresent()) {
            return "이미 사용 중인 닉네임입니다.";
        }

        UserEntity newUser = new UserEntity();
        newUser.setEmail(signUpDto.getEmail());
        newUser.setPassword(signUpDto.getPassword());
        newUser.setNickname(signUpDto.getNickname());
        newUser.setRole("USER");

        userRepository.save(newUser);
        return newUser.getNickname() + "님, 회원가입 완료!";
    }

    // 2. 로그인 로직
    public UserEntity login(LoginDto loginDto) {
        UserEntity findUser = userRepository.findByNickname(loginDto.getNickname())
                .orElse(null);

        if (findUser != null && findUser.getPassword().equals(loginDto.getPassword())) {
            return findUser;
        }
        return null;
    }

    // 3. 아이디(닉네임) 찾기: 이메일 + 비밀번호
    public String findNickname(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .map(UserEntity::getNickname)
                .orElse("일치하는 정보가 없습니다.");
    }

    // 4. 비밀번호 재설정: 닉네임 + 이메일
    public String resetPassword(String nickname, String email, String newPassword) {
        return userRepository.findByNicknameAndEmail(nickname, email)
                .map(user -> {
                    user.setPassword(newPassword);
                    userRepository.save(user);
                    return "비밀번호가 재설정되었습니다.";
                })
                .orElse("닉네임 또는 이메일이 일치하지 않습니다.");
    }
}