package com.example.demo.dto.request;

import com.example.demo.entity.CouponType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Coupon type is required")
    private CouponType type;

    @NotNull(message = "Coupon value is required")
    @DecimalMin(value = "0.0", message = "Coupon value must be >= 0")
    private Double value;

    @DecimalMin(value = "0.0", message = "Minimum order amount must be >= 0")
    private Double minOrderAmount = 0d;

    @DecimalMin(value = "0.0", message = "Max discount must be >= 0")
    private Double maxDiscount = 0d;

    private Integer usageLimit = 0;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Boolean active = true;
}
