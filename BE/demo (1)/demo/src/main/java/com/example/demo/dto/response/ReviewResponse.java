package com.example.demo.dto.response;

import com.example.demo.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponse {
    private String id;
    private String userId;
    private String username;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
            .id(review.getId())
            .userId(review.getUserId())
            .username(review.getUsername())
            .rating(review.getRating())
            .comment(review.getComment())
            .createdAt(review.getCreatedAt())
            .build();
    }
}
