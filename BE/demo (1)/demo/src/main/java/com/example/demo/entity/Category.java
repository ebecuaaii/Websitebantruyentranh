package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    private String id;

    private String name;
    private String icon;
    private String thumbnailUrl;
    private int productCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
