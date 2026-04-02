package com.example.Dto;

import com.example.Entity.LoanEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class LoanResponseDto {
    private Integer loanId;
    private String bookTitle;      // 책 제목 (BookEntity에서 가져옴)
    private String userId;         // 대출자 아이디
    private LocalDateTime loanDate; // 대출일
    private LocalDateTime dueDate;  // 반납 예정일
    private LocalDateTime returnDate; // 실제 반납일
    private Integer overdueDays;    // 연체 일수 (우리가 추가한 컬럼)
    private String status;          // 상태 (BORROWED, RETURNED, OVERDUE)

    // Entity를 DTO로 변환해주는 편의 메서드
    public static LoanResponseDto fromEntity(LoanEntity entity) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setLoanId(entity.getLoanId());
        dto.setBookTitle(entity.getBook().getTitle()); // 연관관계 참조
        dto.setUserId(entity.getUser().getUserId());   // 연관관계 참조
        dto.setLoanDate(entity.getLoanDate());
        dto.setDueDate(entity.getDueDate());
        dto.setReturnDate(entity.getReturnDate());
        dto.setOverdueDays(entity.getOverdueDays());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}