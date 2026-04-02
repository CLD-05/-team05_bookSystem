package com.example.Component;

import com.example.Entity.BookEntity;
import com.example.Repository.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SampleDataLoader {

    private final BookRepository bookRepository;

    @PostConstruct
    public void init() {
        if (bookRepository.findAllActiveBooks().size() < 7) { 
            registerBook("이펙티브 자바", "조슈아 블로크", "자바 플랫폼을 효과적으로 사용하는 방법", "/uploads/sample1.jpg");
            registerBook("토비의 스프링", "이일민", "스프링의 정석과도 같은 책", "/uploads/sample2.jpg");
            registerBook("객체지향의 사실과 오해", "조영호", "객체지향이란 무엇인가에 대한 명쾌한 해답", "/uploads/sample3.jpg");
        }
    }

    private void registerBook(String title, String author, String description, String imageUrl) {
        BookEntity book = new BookEntity();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setImageUrl(imageUrl);
        book.setStatus("AVAILABLE");
        book.setRentalHitCount(0);
        book.setAvgRating(BigDecimal.ZERO);
        bookRepository.save(book);
    }
}
