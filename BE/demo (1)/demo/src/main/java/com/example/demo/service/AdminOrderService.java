package com.example.demo.service;

import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.OrderStatus;

import java.util.List;

public interface AdminOrderService {
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String id);
    OrderResponse updateOrderStatus(String id, OrderStatus status);
}
