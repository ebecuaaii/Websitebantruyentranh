package com.example.demo.service;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CouponResponse;
import com.example.demo.entity.Coupon;

import java.util.List;

public interface CouponService {
    Coupon validateCouponForOrder(String couponCode, double subtotalAmount);
    double calculateDiscountAmount(Coupon coupon, double subtotalAmount);
    void increaseUsage(String couponCode);

    List<CouponResponse> getAllCoupons();
    List<CouponResponse> getActiveCoupons();
    CouponResponse getCouponById(String id);
    CouponResponse createCoupon(CouponRequest request);
    CouponResponse updateCoupon(String id, CouponRequest request);
    void deleteCoupon(String id);
}
