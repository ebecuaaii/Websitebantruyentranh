package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private String id;
    private String userId;
    private List<OrderItem> items = new ArrayList<>();
    private double totalAmount;
    private OrderStatus status = OrderStatus.PENDING;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
