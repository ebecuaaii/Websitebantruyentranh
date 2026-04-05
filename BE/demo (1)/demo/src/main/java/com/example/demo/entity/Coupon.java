package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    private String id;

    private String code;
    private CouponType type;
    private Double value;

    @Builder.Default
    private Double minOrderAmount = 0d;

    @Builder.Default
    private Double maxDiscount = 0d;

    @Builder.Default
    private Integer usageLimit = 0;

    @Builder.Default
    private Integer usedCount = 0;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
