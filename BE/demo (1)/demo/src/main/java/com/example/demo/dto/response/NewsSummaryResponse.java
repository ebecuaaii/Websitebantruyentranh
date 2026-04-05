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
public class NewsSummaryResponse {
    private Long id;
    private String title;
    private String slug;
    private String thumbnail;
    private String summary;
    private NewsResponse.Category category;
    @JsonProperty("published_at")
    private Instant publishedAt;
    private List<String> tags;

    public static NewsSummaryResponse from(News news) {
        return NewsSummaryResponse.builder()
            .id(news.getId())
            .title(news.getTitle())
            .slug(news.getSlug())
            .thumbnail(news.getThumbnail())
            .summary(news.getSummary())
            .category(NewsResponse.Category.from(news.getCategory()))
            .publishedAt(news.getPublishedAt())
            .tags(news.getTags())
            .build();
    }
}
