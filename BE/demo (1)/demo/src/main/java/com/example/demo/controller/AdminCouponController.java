package com.example.demo.controller;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public BaseResponse<?> getAllCoupons() {
        return BaseResponse.success(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public BaseResponse<?> getCoupon(@PathVariable String id) {
        return BaseResponse.success(couponService.getCouponById(id));
    }

    @PostMapping
    public BaseResponse<?> createCoupon(@Valid @RequestBody CouponRequest request) {
        return BaseResponse.success(couponService.createCoupon(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<?> updateCoupon(@PathVariable String id, @Valid @RequestBody CouponRequest request) {
        return BaseResponse.success(couponService.updateCoupon(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<?> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return BaseResponse.success(null);
    }
}
