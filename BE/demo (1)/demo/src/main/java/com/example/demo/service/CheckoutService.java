package com.example.demo.service;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.request.CouponPreviewRequest;
import com.example.demo.dto.response.CheckoutResponse;
import com.example.demo.dto.response.CouponPreviewResponse;
import com.example.demo.dto.response.PaymentCallbackResult;

import java.util.Map;

public interface CheckoutService {
    CheckoutResponse checkout(String userId, CheckoutRequest request, String clientIp);
    CouponPreviewResponse previewCoupon(String userId, CouponPreviewRequest request);
    PaymentCallbackResult processMomoCallback(Map<String, String> payload);
    PaymentCallbackResult processVnpayCallback(Map<String, String> payload);
}
