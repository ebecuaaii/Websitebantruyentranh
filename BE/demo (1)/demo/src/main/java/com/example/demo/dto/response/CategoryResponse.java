package com.example.demo.dto.response;

import com.example.demo.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String icon;
    private String thumbnailUrl;
    private int productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .icon(category.getIcon())
            .thumbnailUrl(category.getThumbnailUrl())
            .productCount(category.getProductCount())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}
