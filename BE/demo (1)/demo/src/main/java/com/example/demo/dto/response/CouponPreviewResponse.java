package com.example.demo.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPreviewResponse {
    private String couponCode;
    private double subtotalAmount;
    private double shippingFee;
    private double discount;
    private double totalAmount;
}
