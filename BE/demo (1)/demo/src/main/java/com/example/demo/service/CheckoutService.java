package com.example.demo.service;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Coupon;

public interface CheckoutService {
    OrderResponse checkout(String userId, CheckoutRequest request);
    Coupon validateCoupon(String code, Double orderAmount);
}
