package com.example.demo.controller;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "2.4 Checkout – Phúc")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final JwtService jwtService;

    private String extractUserId(HttpServletRequest request) {
        return jwtService.extractUserId(request.getHeader("Authorization").substring(7));
    }

    @Operation(summary = "Đặt hàng từ giỏ hàng", security = @SecurityRequirement(name = "Bearer Auth"))
    @PostMapping("/checkout")
    public ResponseEntity<BaseResponse<?>> checkout(@Valid @RequestBody CheckoutRequest request,
                                                     HttpServletRequest httpRequest) {
        return ResponseEntity.ok(BaseResponse.success(checkoutService.checkout(extractUserId(httpRequest), request)));
    }

    @Operation(summary = "Kiểm tra mã giảm giá", security = @SecurityRequirement(name = "Bearer Auth"))
    @PostMapping("/apply-coupon")
    public ResponseEntity<BaseResponse<?>> applyCoupon(@RequestParam String code,
                                                        @RequestParam Double orderAmount) {
        return ResponseEntity.ok(BaseResponse.success(checkoutService.validateCoupon(code, orderAmount)));
    }
}
