package com.example.Repository;

import com.example.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByNickname(String nickname);
    Optional<UserEntity> findByEmail(String email);

    // 아이디 찾기용: 이메일과 비밀번호가 일치하는 사용자 찾기
    Optional<UserEntity> findByEmailAndPassword(String email, String password);

    // 비밀번호 재설정용: 닉네임과 이메일이 일치하는 사용자 찾기
    Optional<UserEntity> findByNicknameAndEmail(String nickname, String email);
}