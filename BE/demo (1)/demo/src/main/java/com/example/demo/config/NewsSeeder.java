package com.example.demo.config;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsSeeder implements CommandLineRunner {

    private final NewsRepository newsRepository;

    @Override
    public void run(String... args) {
        if (newsRepository.existsById(101L)) {
            return;
        }

        News sample = News.builder()
            .id(101L)
            .title("Top 5 cuốn sách kinh tế đáng đọc nhất đầu năm 2026")
            .slug("top-5-sach-kinh-te-2026")
            .thumbnail("https://cdn.example.com/images/news-01.jpg")
            .summary("Điểm qua những đầu sách giúp bạn thay đổi tư duy tài chính trong năm mới.")
            .content("<html>... nội dung bài viết ...</html>")
            .category(News.NewsCategory.builder().id(1L).name("Review Sách").build())
            .author(News.NewsAuthor.builder()
                .name("Nguyễn Văn A")
                .avatar("https://cdn.example.com/avatars/user-a.jpg")
                .build())
            .publishedAt(Instant.parse("2026-03-30T10:00:00Z"))
            .relatedBooks(List.of(
                News.NewsRelatedBook.builder()
                    .id("book-99")
                    .title("Tư Duy Thịnh Vượng")
                    .price(150000)
                    .image("https://cdn.example.com/books/book-99.jpg")
                    .build()
            ))
            .tags(List.of("kinh tế", "tài chính", "sách hay 2026"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        newsRepository.save(sample);
    }
}
