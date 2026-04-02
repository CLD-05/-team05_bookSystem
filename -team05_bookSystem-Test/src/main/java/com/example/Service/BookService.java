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

    // [관리자용] 2. 도서 삭제 (논리 삭제)
    @Transactional
    public void deleteBook(Integer bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다."));

        if ("RENTED".equals(book.getStatus())) {
            throw new IllegalStateException("현재 대여 중인 도서는 삭제(폐기) 처리할 수 없습니다.");
        }

        // 상태만 'DELETED'로 변경하여 검색에서 제외되도록 함
        book.setStatus("DELETED");
    }

    // [유저용] 3. 전체 도서 목록 조회 (삭제된 도서 제외)
    @Transactional(readOnly = true)
    public List<BookEntity> getAllActiveBooks() {
        // Repository에 만든 findAllActiveBooks() 사용
        return bookRepository.findAllActiveBooks();
    }

    // [유저용] 4. 제목으로 검색 (삭제된 도서 제외)
    @Transactional(readOnly = true)
    public List<BookEntity> searchByTitle(String title) {
        // StatusNot 메서드를 사용하여 "DELETED"가 아닌 것만 조회
        return bookRepository.findByStatusNotAndTitleContaining("DELETED", title);
    }

    // [유저용] 5. 저자로 검색 (삭제된 도서 제외)
    @Transactional(readOnly = true)
    public List<BookEntity> searchByAuthor(String author) {
        // StatusNot 메서드를 사용하여 "DELETED"가 아닌 것만 조회
        return bookRepository.findByStatusNotAndAuthorContaining("DELETED", author);
    }
}