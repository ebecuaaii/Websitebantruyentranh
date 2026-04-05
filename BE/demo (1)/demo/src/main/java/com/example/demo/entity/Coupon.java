package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String description;
    private Double discountPercent;
    private Double discountAmount;
    private Double minOrderAmount;
    private Integer maxUsage;
    private Integer usedCount = 0;
    private boolean active = true;

    private LocalDateTime expiresAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
