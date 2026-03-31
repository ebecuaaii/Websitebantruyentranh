package com.example.demo.controller;

import com.example.demo.dto.request.UpdateOrderStatusRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public BaseResponse<List<OrderResponse>> getAllOrders() {
        return BaseResponse.success(adminOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public BaseResponse<OrderResponse> getOrder(@PathVariable String id) {
        return BaseResponse.success(adminOrderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    public BaseResponse<OrderResponse> updateStatus(
            @PathVariable String id, 
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return BaseResponse.success(adminOrderService.updateOrderStatus(id, request.getStatus()));
    }
}
