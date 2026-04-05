package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class NewsRequest {

    @NotBlank(message = "Tiêu đề là bắt buộc")
    private String title;

    @NotBlank(message = "Slug là bắt buộc")
    private String slug;

    private String thumbnail;
    private String summary;
    private String content;

    @NotNull(message = "Category là bắt buộc")
    @Valid
    private Category category;

    @NotNull(message = "Author là bắt buộc")
    @Valid
    private Author author;

    private Instant publishedAt;
    private List<RelatedBook> relatedBooks;
    private List<String> tags;

    @Getter
    @Setter
    public static class Category {
        @NotNull(message = "Category id là bắt buộc")
        private Long id;

        @NotBlank(message = "Category name là bắt buộc")
        private String name;
    }

    @Getter
    @Setter
    public static class Author {
        @NotBlank(message = "Tên tác giả là bắt buộc")
        private String name;
        private String avatar;
    }

    @Getter
    @Setter
    public static class RelatedBook {
        @NotBlank(message = "Book id là bắt buộc")
        private String id;
        @NotBlank(message = "Book title là bắt buộc")
        private String title;
        private double price;
        private String image;
    }
}
