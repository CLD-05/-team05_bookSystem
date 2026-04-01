package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable; // 추가됨

import java.time.LocalDateTime;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
// Persistable 인터페이스를 구현해야 문자열 ID를 직접 넣을 때 에러가 안 납니다.
public class UserEntity implements Persistable<String> {

    @Column(nullable = false)
    private String role = "USER"; // 기본값 설정

    @Id
    // @GeneratedValue 줄을 삭제했습니다. 직접 ID를 입력받기 때문입니다.
    @Column(name = "user_id")
    private String userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String nickname;


    @Column(name = "max_rental_limit")
    private Integer maxRentalLimit = 3;

    @Column(name = "current_rental_count")
    private Integer currentRentalCount = 0;

    @Column(name = "total_rental_count")
    private Integer totalRentalCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- Persistable 인터페이스 구현 부분 ---

    @Override
    public String getId() {
        return this.userId;
    }

    @Override
    public boolean isNew() {
        // 이 로직이 있어야 JPA가 UPDATE가 아닌 INSERT 쿼리를 날립니다.
        return this.createdAt == null;
    }
}