package com.example.Controller;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Entity.BookEntity;
import com.example.Entity.UserEntity;
import com.example.Service.BookService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
    private BookService bookService;

    // 1. 메인 페이지 (index.html) 연결
    @GetMapping("/")
    public String index() {
    	
        return "index";
    }

    // 2. 마이페이지 (mypage.html) 연결
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
    	UserEntity user = new UserEntity(); // 빈 상자를 먼저 만듭니다. (@NoArgsConstructor 덕분에 가능)
    	user.setUserId("test_id");          // 아이디 넣기
    	user.setNickname("테스터");         // 닉네임 넣기
    	user.setEmail("test@test.com");     // 이메일 넣기
    	user.setPassword("1234");           // 패스워드 넣기

    	model.addAttribute("user", user);
    	model.addAttribute("rentedBooks", new ArrayList<>());
    	model.addAttribute("allRentalHistory", new ArrayList<>());
    	
        return "mypage"; 
    }

    @GetMapping("/return")
    public String returnPage() {
        return "return";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/";
    }
    
    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String title, 
                             @RequestParam(required = false) String author, 
                             Model model) {
    	// 1. 서비스에게 "이 제목과 저자로 검색해줘"라고 시킵니다.
    	List<BookEntity> searchResults = bookService.searchByTitleAndAuthor(title, author);
        
        // 2. 검색 결과를 검색 결과 페이지(book_search.html)로 보냅니다.
        model.addAttribute("searchResults", searchResults);
        
        // 3. 입력했던 검색어도 다시 보내주면 "XX에 대한 검색 결과입니다"라고 보여주기 좋습니다.
        model.addAttribute("queryTitle", title);
        
        return "book_search"; // 이 파일이 열리면서 데이터가 전달됩니다.
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login"; 
    }
    
    
}
