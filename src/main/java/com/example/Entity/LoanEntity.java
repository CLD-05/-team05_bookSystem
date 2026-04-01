package com.example.Entity;

import jakarta.persistence.*;
        import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Getter @Setter @NoArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Integer loanId;

    // FK: UserEntity와 연결 (단순 String ID 대신 객체 사용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // FK: BookEntity와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @Column(name = "simple_description")
    private String simpleDescription;

    @CreationTimestamp
    @Column(name = "loan_date", updatable = false)
    private LocalDateTime loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(14);

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    private Double rating; // 평점 (1~5)

    @Column(name = "review_content", length =50, columnDefinition = "TEXT")
    private String reviewContent; // 리뷰 내용

    @Column(nullable = false)
    private String status = "BORROWED"; // BORROWED, RETURNED
}
