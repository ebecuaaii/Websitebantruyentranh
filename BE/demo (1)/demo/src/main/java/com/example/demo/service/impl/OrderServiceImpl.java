package com.example.demo.service.impl;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public List<OrderResponse> getMyOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getMyOrderDetail(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUserId().equals(userId))
            throw new RuntimeException("Access denied");
        return OrderResponse.from(order);
    }

    @Override
    public DashboardResponse getDashboard() {
        List<Order> all = orderRepository.findAll();
        return DashboardResponse.builder()
            .totalOrders((long) all.size())
            .totalUsers(userRepository.count())
            .totalRevenue(all.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount).sum())
            .pendingOrders(all.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count())
            .deliveredOrders(all.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count())
            .cancelledOrders(all.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())
            .build();
    }

    @Override
    public DashboardResponse getRevenueStats(String from, String to) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<Order> orders = orderRepository.findDeliveredOrdersBetween(
            LocalDateTime.parse(from + "T00:00:00", fmt),
            LocalDateTime.parse(to + "T23:59:59", fmt)
        );
        Map<String, Double> revenueByMonth = orders.stream().collect(
            Collectors.groupingBy(
                o -> o.getCreatedAt().getMonth().toString(),
                Collectors.summingDouble(Order::getTotalAmount)
            )
        );
        return DashboardResponse.builder()
            .revenueByMonth(revenueByMonth)
            .totalRevenue(orders.stream().mapToDouble(Order::getTotalAmount).sum())
            .build();
    }

    @Override
    public DashboardResponse getOrderStats(String from, String to) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<Order> orders = orderRepository.findByCreatedAtBetween(
            LocalDateTime.parse(from + "T00:00:00", fmt),
            LocalDateTime.parse(to + "T23:59:59", fmt)
        );
        Map<String, Long> byStatus = orders.stream().collect(
            Collectors.groupingBy(o -> o.getStatus().toString(), Collectors.counting())
        );
        return DashboardResponse.builder()
            .totalOrders((long) orders.size())
            .ordersByStatus(byStatus)
            .build();
    }
}
