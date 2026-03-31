package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String id;

    private String name;
    private String author; 
    private String imageUrl;
    private String categoryId;
    private String status = "available";
    private double price = 0.0;
    private int quantity = 0;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
