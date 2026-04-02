package com.example.Controller;

import com.example.Entity.BookEntity;
import com.example.Entity.LoanEntity;
import com.example.Service.AdminService;
import com.example.Service.BookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller; // @RestController에서 변경
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // 화면을 반환하기 위해 Controller로 변경
@RequestMapping("/admin") // /api/admin 대신 관리자용 경로 설정
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;
    private final AdminService adminService;

    // 권한 체크 공통 메소드 (타임리프 리다이렉트 방식)
    private boolean isNotAdmin(HttpSession session) {
        return !"ADMIN".equals(session.getAttribute("userRole"));
    }

    /**
     * 1. 관리자 메인 페이지 (admin_index.html)
     * 도서 목록을 보여주고 삭제 기능을 포함함
     */
    @GetMapping("/books")
    public String adminIndex(HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/user/login"; // 권한 없으면 로그인창으로

        List<BookEntity> books = bookService.getAllActiveBooks(); // 삭제되지 않은 도서 전체
        model.addAttribute("books", books);
        return "admin_index"; // templates/admin_index.html 호출
    }

    /**
     * 2. 도서 등록 페이지 이동 (admin_book_add.html)
     */
    @GetMapping("/books/add")
    public String addBookPage(HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/user/login";
        return "admin_book_add"; // templates/admin_book_add.html 호출
    }

    /**
     * 3. 실제 도서 등록 처리 (Form Submit 방식)
     */
    @PostMapping("/books/register")
    public String registerBook(@ModelAttribute BookEntity bookEntity,
                               @RequestParam("imageFile") org.springframework.web.multipart.MultipartFile imageFile, // 1. 이거 추가!
                               HttpSession session) {

        if (isNotAdmin(session)) return "redirect:/user/login";

        try {
            // 2. 서비스 호출 시 이미지 파일도 함께 넘기도록 수정 (Service 코드도 수정 필요)
            bookService.registerBook(bookEntity, imageFile);
            return "redirect:/admin/books";
        } catch (Exception e) {
            // 3. HTML 하단의 th:if="${param.error}"를 작동시키기 위해 에러 파라미터 추가
            return "redirect:/admin/books/add?error=fail";
        }
    }
    /**
     * 4. 도서 삭제 처리
     */
    @PostMapping("/books/delete/{bookId}") // DeleteMapping 대신 PostMapping(HTML Form 호환)
    public String deleteBook(@PathVariable Integer bookId, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/user/login";

        try {
            bookService.deleteBook(bookId);
            return "redirect:/admin/books";
        } catch (Exception e) {
            try {
                return "redirect:/admin/books?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/admin/books?error=delete_failed";
            }
        }
    }

    /**
     * 5. 사용자 및 대출 관리 페이지 (admin_user.html)
     */
    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/user/login";

        // 사용자별 대출 현황 데이터를 가져와 모델에 담음
        model.addAttribute("userLoans", adminService.getAllLoanHistory());
        return "admin_user"; // templates/admin_user.html 호출
    }
}