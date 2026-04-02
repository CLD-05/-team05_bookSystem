package com.example.Repository;

import com.example.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Integer -> String으로 변경
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByEmailAndNickname(String email, String nickname);

    // 서비스에서 호출하는 이름과 똑같이 맞춤 (이메일 + 아이디)
    Optional<UserEntity> findByEmailAndUserId(String email, String userId);

    // 애초에 권한이 'USER'인 데이터 중에서만 이메일/닉네임을 검색
    Optional<UserEntity> findByEmailAndNicknameAndRole(String email, String nickname, String role);
}