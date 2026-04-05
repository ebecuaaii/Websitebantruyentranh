package com.example.demo.dto.response;

import com.example.demo.entity.Coupon;
import com.example.demo.entity.CouponType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private String id;
    private String code;
    private CouponType type;
    private double value;
    private double minOrderAmount;
    private double maxDiscount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CouponResponse from(Coupon coupon) {
        return CouponResponse.builder()
            .id(coupon.getId())
            .code(coupon.getCode())
            .type(coupon.getType())
            .value(coupon.getValue() == null ? 0d : coupon.getValue())
            .minOrderAmount(coupon.getMinOrderAmount() == null ? 0d : coupon.getMinOrderAmount())
            .maxDiscount(coupon.getMaxDiscount() == null ? 0d : coupon.getMaxDiscount())
            .usageLimit(coupon.getUsageLimit())
            .usedCount(coupon.getUsedCount())
            .startAt(coupon.getStartAt())
            .endAt(coupon.getEndAt())
            .active(Boolean.TRUE.equals(coupon.getActive()))
            .createdAt(coupon.getCreatedAt())
            .updatedAt(coupon.getUpdatedAt())
            .build();
    }
}
