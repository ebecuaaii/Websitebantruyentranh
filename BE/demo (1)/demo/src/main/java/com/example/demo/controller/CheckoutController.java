package com.example.demo.controller;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.request.CouponPreviewRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final JwtService jwtService;

    @PostMapping("/preview-coupon")
    public ResponseEntity<BaseResponse<?>> previewCoupon(
        @Valid @RequestBody CouponPreviewRequest request,
        HttpServletRequest servletRequest
    ) {
        String userId = extractUserId(servletRequest);
        return ResponseEntity.ok(BaseResponse.success(checkoutService.previewCoupon(userId, request)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<?>> checkout(
        @Valid @RequestBody CheckoutRequest request,
        HttpServletRequest servletRequest
    ) {
        String userId = extractUserId(servletRequest);
        String clientIp = extractClientIp(servletRequest);
        return ResponseEntity.ok(BaseResponse.success(checkoutService.checkout(userId, request, clientIp)));
    }

    private String extractUserId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        String token = authorization.substring(7);
        return jwtService.extractUserId(token);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
