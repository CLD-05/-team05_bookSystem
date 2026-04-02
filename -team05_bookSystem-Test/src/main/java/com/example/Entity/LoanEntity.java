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

//    @Column(name = "due_date", nullable = false)
//    private LocalDateTime dueDate = LocalDateTime.now().plusDays(14);
    
    //[수정] 실제 비즈니스 로직(Service)에서 대출 시점 기준으로 다시 계산해주는 것이 더 정확합니다.
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;
    
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    
    // [추가] 연체 일수 저장 컬럼
    @Column(name = "overdue_days")
    private Integer overdueDays = 0;

    private Double rating; // 평점 (1~5)
    
    @Column(name = "review_content", length =50, columnDefinition = "TEXT")
    private String reviewContent; // 리뷰 내용

//    @Column(nullable = false)
//    private String status = "BORROWED"; // BORROWED, RETURNED
    
    // [수정] 상태값에 'OVERDUE'를 추가하여 관리하는 것을 추천합니다.
    // BORROWED(대출중), RETURNED(반납완료), OVERDUE(연체중)
    @Column(nullable = false)
    private String status = "BORROWED";
    
    // --- 비즈니스 편의 메서드 추가 ---
    /*
     * [추가] 연체 여부 확인 메서드
     * 반납 전이면서, 현재 시간이 반납 예정일보다 늦었는지 확인
     */
    public boolean checkIsOverdue() {
        return this.returnDate == null && LocalDateTime.now().isAfter(this.dueDate);
    }
}
