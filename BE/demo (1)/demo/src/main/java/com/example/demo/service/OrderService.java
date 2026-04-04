package com.example.demo.service;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {

    // Customer
    List<OrderResponse> getMyOrders(String userId);
    OrderResponse getMyOrderDetail(String orderId, String userId);

    // Admin Dashboard
    DashboardResponse getDashboard();
    DashboardResponse getRevenueStats(String from, String to);
    DashboardResponse getOrderStats(String from, String to);
}
