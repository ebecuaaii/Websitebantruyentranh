package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/active")
    public BaseResponse<?> getActiveCoupons() {
        return BaseResponse.success(couponService.getActiveCoupons());
    }
}
