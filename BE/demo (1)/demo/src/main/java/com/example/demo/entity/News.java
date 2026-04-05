package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    private Long id;

    private String title;
    private String slug;
    private String thumbnail;
    private String summary;
    private String content;

    private NewsCategory category;
    private NewsAuthor author;

    private Instant publishedAt;
    private List<NewsRelatedBook> relatedBooks;
    private List<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewsCategory {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewsAuthor {
        private String name;
        private String avatar;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewsRelatedBook {
        private String id;
        private String title;
        private double price;
        private String image;
    }
}
