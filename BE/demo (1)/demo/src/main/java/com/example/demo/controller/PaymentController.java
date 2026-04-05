package com.example.demo.controller;

import com.example.demo.dto.response.PaymentCallbackResult;
import com.example.demo.service.CheckoutService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${payment.checkout-result-url:http://localhost:5500/FE/pages/checkout.html}")
    private String checkoutResultUrl;

    private final CheckoutService checkoutService;

    @PostMapping("/momo/ipn")
    public ResponseEntity<Map<String, Object>> momoIpn(@RequestBody Map<String, Object> payload) {
        Map<String, String> normalized = normalizePayload(payload);
        try {
            checkoutService.processMomoCallback(normalized);
            return ResponseEntity.ok(Map.of("resultCode", 0, "message", "success"));
        } catch (Exception ex) {
            return ResponseEntity.ok(Map.of("resultCode", 1, "message", ex.getMessage()));
        }
    }

    @GetMapping("/momo/return")
    public void momoReturn(@RequestParam Map<String, String> payload, HttpServletResponse response) throws IOException {
        PaymentCallbackResult result;
        try {
            result = checkoutService.processMomoCallback(payload);
        } catch (Exception ex) {
            result = PaymentCallbackResult.builder()
                .gateway("MOMO")
                .success(false)
                .message(ex.getMessage())
                .build();
        }
        response.sendRedirect(buildRedirectUrl(result));
    }

    @GetMapping("/vnpay/return")
    public void vnpayReturn(@RequestParam Map<String, String> payload, HttpServletResponse response) throws IOException {
        PaymentCallbackResult result;
        try {
            result = checkoutService.processVnpayCallback(payload);
        } catch (Exception ex) {
            result = PaymentCallbackResult.builder()
                .gateway("VNPAY")
                .success(false)
                .message(ex.getMessage())
                .build();
        }
        response.sendRedirect(buildRedirectUrl(result));
    }

    private Map<String, String> normalizePayload(Map<String, Object> payload) {
        Map<String, String> normalized = new HashMap<>();
        payload.forEach((key, value) -> normalized.put(key, value == null ? "" : String.valueOf(value)));
        return normalized;
    }

    private String buildRedirectUrl(PaymentCallbackResult result) {
        StringBuilder url = new StringBuilder(checkoutResultUrl);
        url.append(checkoutResultUrl.contains("?") ? "&" : "?");
        url.append("paymentStatus=").append(result.isSuccess() ? "success" : "failed");
        if (result.getOrderId() != null) {
            url.append("&orderId=").append(encode(result.getOrderId()));
        }
        if (result.getGateway() != null) {
            url.append("&gateway=").append(encode(result.getGateway()));
        }
        if (result.getMessage() != null) {
            url.append("&message=").append(encode(result.getMessage()));
        }
        return url.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
