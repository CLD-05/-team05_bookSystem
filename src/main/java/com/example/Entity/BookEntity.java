package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId; // PK, Auto Increment

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String author; // 저자

    @Column(name = "description", length = 500)
    private String description; // 도서 한줄 설명

    @Column(name = "image_url", length = 500)
    private String imageUrl; // 표지 이미지

    @Column(nullable = false)
    private String status = "AVAILABLE"; // 'AVAILABLE', 'RENTED'

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO; // 평균 평점 (기본값 0.0)

    @Column(name = "rental_hit_count")
    private Integer rentalHitCount = 0; // 대출 횟수

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록 일시
}