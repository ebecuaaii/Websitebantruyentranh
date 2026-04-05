package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponPreviewRequest {

    @NotBlank(message = "Coupon code is required")
    private String couponCode;

    @DecimalMin(value = "0.0", message = "Shipping fee must be >= 0")
    private Double shippingFee = 0d;
}
