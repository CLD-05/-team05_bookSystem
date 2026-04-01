package com.example.Service;

import com.example.Entity.BookEntity;
import com.example.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // [관리자용] 1. 새 도서 등록
    @Transactional
    public BookEntity registerBook(BookEntity bookEntity) {
        bookEntity.setStatus("AVAILABLE"); // 초기값 강제 설정
        bookEntity.setRentalHitCount(0);
        bookEntity.setAvgRating(new java.math.BigDecimal("0.0"));
        return bookRepository.save(bookEntity);
    }

    // [관리자용] 2. 도서 삭제
    @Transactional
    public void deleteBook(Integer bookId) {
        // 1. 책 존재 확인
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다."));

        // 2. 현재 대여 중인지 확인 (대여 중이면 '삭제 처리'도 안 됨)
        if ("RENTED".equals(book.getStatus())) {
            throw new IllegalStateException("현재 대여 중인 도서는 삭제(폐기) 처리할 수 없습니다.");
        }

        // 3. [핵심] 실제 삭제 대신 상태만 'DELETED'로 변경
        book.setStatus("DELETED");
        // 실제 DB에서 지우지 않으므로 외래 키 에러가 발생하지 않습니다!
    }
    // [유저용] 3. 전체 도서 목록 조회
    @Transactional(readOnly = true)
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    // [유저용] 4. 제목으로 검색
    @Transactional(readOnly = true)
    public List<BookEntity> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    // [유저용] 5. 저자로 검색
    @Transactional(readOnly = true)
    public List<BookEntity> searchByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }
}