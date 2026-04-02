package com.example.Service;

import com.example.Entity.BookEntity;
import com.example.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    // ---------------- [여기서부터 새로 추가 및 정리된 부분] ----------------

    // [유저용] 6. 인기도서 TOP 5 가져오기
    @Transactional(readOnly = true)
    public List<BookEntity> getTop5PopularBooks() {
        return bookRepository.findTop5ByStatusNotOrderByRentalHitCountDescAvgRatingDesc("DELETED");
    }

    // [유저용] 7. 도서 상세 정보 가져오기 (ID로 조회)
    @Transactional(readOnly = true)
    public BookEntity getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. id=" + id));
    }

    public void registerBook(BookEntity book, MultipartFile imageFile) {
        // 1. 이미지가 비어있지 않다면 서버 폴더나 S3에 저장
        if (!imageFile.isEmpty()) {
            String fileName = saveImage(imageFile); // 파일 저장 로직
            book.setImageUrl("/uploads/" + fileName); // DB에는 저장된 경로만 기록
        }
        bookRepository.save(book);
    }

} // <--- 여기가 클래스가 진짜 끝나는 괄호입니다!