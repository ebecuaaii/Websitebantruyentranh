package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id
    private String id;

    private String userId;
    private List<OrderItem> items;
    private Double totalAmount;
    private String couponCode;
    private Double discount;

    private OrderStatus status = OrderStatus.PENDING;

    private String shippingAddress;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }
}
