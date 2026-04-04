package com.example.demo.dto.response;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {
    private Long totalOrders;
    private Long totalUsers;
    private Double totalRevenue;
    private Long pendingOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private Map<String, Double> revenueByMonth;
    private Map<String, Long> ordersByStatus;
}
