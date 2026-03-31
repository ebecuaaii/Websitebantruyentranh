package com.example.demo.dto.response;

import com.example.demo.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String author; 
    private String imageUrl;
    private String categoryId;
    private String categoryName;
    private String status;
    private double price;
    private int quantity;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product product, String categoryName) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .author(product.getAuthor())
            .imageUrl(product.getImageUrl())
            .categoryId(product.getCategoryId())
            .categoryName(categoryName)
            .status(product.getStatus())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .description(product.getDescription())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
}
