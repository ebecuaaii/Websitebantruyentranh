package com.example.demo.service;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.entity.Coupon;
import java.util.List;

public interface CouponService {
    List<Coupon> getAll();
    Coupon getById(String id);
    Coupon create(CouponRequest request);
    Coupon update(String id, CouponRequest request);
    void delete(String id);
    Coupon validate(String code, Double orderAmount);
}
