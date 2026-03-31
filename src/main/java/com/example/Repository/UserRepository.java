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
}