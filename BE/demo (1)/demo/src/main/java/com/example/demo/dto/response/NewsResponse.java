package com.example.demo.dto.response;

import com.example.demo.entity.News;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {
    private Long id;
    private String title;
    private String slug;
    private String thumbnail;
    private String summary;
    private String content;
    private Category category;
    private Author author;
    @JsonProperty("published_at")
    private Instant publishedAt;
    @JsonProperty("related_books")
    private List<RelatedBook> relatedBooks;
    private List<String> tags;

    public static NewsResponse from(News news) {
        return NewsResponse.builder()
            .id(news.getId())
            .title(news.getTitle())
            .slug(news.getSlug())
            .thumbnail(news.getThumbnail())
            .summary(news.getSummary())
            .content(news.getContent())
            .category(Category.from(news.getCategory()))
            .author(Author.from(news.getAuthor()))
            .publishedAt(news.getPublishedAt())
            .relatedBooks(RelatedBook.from(news.getRelatedBooks()))
            .tags(news.getTags())
            .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Category {
        private Long id;
        private String name;

        public static Category from(News.NewsCategory category) {
            if (category == null) {
                return null;
            }
            return Category.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Author {
        private String name;
        private String avatar;

        public static Author from(News.NewsAuthor author) {
            if (author == null) {
                return null;
            }
            return Author.builder()
                .name(author.getName())
                .avatar(author.getAvatar())
                .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RelatedBook {
        private String id;
        private String title;
        private double price;
        private String image;

        public static List<RelatedBook> from(List<News.NewsRelatedBook> books) {
            if (books == null) {
                return List.of();
            }
            return books.stream()
                .map(book -> RelatedBook.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .price(book.getPrice())
                    .image(book.getImage())
                    .build())
                .toList();
        }
    }
}
