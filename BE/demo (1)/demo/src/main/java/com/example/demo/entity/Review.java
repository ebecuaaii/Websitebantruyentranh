package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    private String id;
    
    private String productId;
    private String userId;
    private String username;
    
    private int rating; // 1-5 sao
    private String comment;
    
    private LocalDateTime createdAt;
}
