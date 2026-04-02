package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Service.BookService;
import com.example.Service.LoanService;
import com.example.Dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books") // 통일성을 위해 /books 로 맞춤
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final LoanService loanService;

    /**
     * [수정됨] 메인 페이지 (index.html) 연동
     * DB의 Top5 쿼리를 활용해 대여 횟수 및 평점 순으로 인기 도서 5권을 가져옵니다.
     */
    @GetMapping("/index")
    public String index(Model model) {
        // 매의 눈으로 찾아내신 바로 그 부분 수정!
        List<BookEntity> popularBooks = bookService.getTop5PopularBooks();
        model.addAttribute("popularBooks", popularBooks);
        return "index";
    }

    /**
     * 도서 검색 및 전체 리스트 페이지 연동
     */
    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "title") String type,
                              Model model) {
        List<BookEntity> books;

        if (keyword != null && !keyword.isEmpty()) {
            if ("author".equals(type)) {
                books = bookService.searchByAuthor(keyword);
            } else {
                books = bookService.searchByTitle(keyword);
            }
        } else {
            books = bookService.getAllActiveBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", type); // 검색 타입 유지용 추가
        return "book_search";
    }

    /**
     * 도서 반납 후 리뷰 작성을 위한 페이지 연동
     */
    @GetMapping("/return")
    public String returnReviewPage(@RequestParam Integer bookId, Model model) {
        model.addAttribute("bookId", bookId);
        return "return";
    }

    /**
     * 도서 상세 페이지 연동
     */
    @GetMapping("/detail/{id}")
    public String getBookDetail(@PathVariable Integer id, Model model) { // Long -> Integer로 수정 (Entity에 맞춤)
        BookEntity book = bookService.getBookById(id);
        List<ReviewResponseDto> reviews = loanService.getBookReviews(id);

        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        return "book_detail";
    }
}