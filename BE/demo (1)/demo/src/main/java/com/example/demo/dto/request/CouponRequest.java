package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class CouponRequest {
    @NotBlank(message = "Mã coupon không được để trống")
    private String code;
    private String description;
    private Double discountPercent;
    private Double discountAmount;
    private Double minOrderAmount;
    private Integer maxUsage;
    private boolean active = true;
    private LocalDateTime expiresAt;
}
