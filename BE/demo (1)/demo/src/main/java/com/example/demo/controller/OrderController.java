package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "2.5 Dashboard & Orders – Trúc")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    private String extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractUserId(token);
    }

    // ── Customer ──────────────────────────────────────────
    @Operation(summary = "Danh sách đơn hàng cá nhân", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<?>> getMyOrders(HttpServletRequest request) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getMyOrders(extractUserId(request))));
    }

    @Operation(summary = "Chi tiết đơn hàng cá nhân", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/orders/{id}")
    public ResponseEntity<BaseResponse<?>> getMyOrderDetail(@PathVariable String id,
                                                             HttpServletRequest request) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getMyOrderDetail(id, extractUserId(request))));
    }

    // ── Admin Dashboard ───────────────────────────────────
    @Operation(summary = "Dashboard thống kê tổng quan", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/admin/dashboard")
    public ResponseEntity<BaseResponse<?>> getDashboard() {
        return ResponseEntity.ok(BaseResponse.success(orderService.getDashboard()));
    }

    @Operation(summary = "Thống kê doanh thu", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/admin/dashboard/revenue")
    public ResponseEntity<BaseResponse<?>> getRevenue(@RequestParam String from,
                                                       @RequestParam String to) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getRevenueStats(from, to)));
    }

    @Operation(summary = "Thống kê số lượng đơn hàng", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/admin/dashboard/orders-stats")
    public ResponseEntity<BaseResponse<?>> getOrderStats(@RequestParam String from,
                                                          @RequestParam String to) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderStats(from, to)));
    }
}
