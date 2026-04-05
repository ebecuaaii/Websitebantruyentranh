package com.example.demo.dto.response;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private List<OrderItemResponse> items;
    private double subtotalAmount;
    private double shippingFee;
    private String shippingMethod;
    private double totalAmount;
    private String couponCode;
    private double discount;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentProvider;
    private String paymentTxnRef;
    private String paymentTransactionId;
    private String paymentMessage;
    private LocalDateTime paidAt;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemResponse {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
    }

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .subtotalAmount(order.getSubtotalAmount() == null ? 0d : order.getSubtotalAmount())
            .shippingFee(order.getShippingFee() == null ? 0d : order.getShippingFee())
            .shippingMethod(order.getShippingMethod())
            .totalAmount(order.getTotalAmount() == null ? 0d : order.getTotalAmount())
            .couponCode(order.getCouponCode())
            .discount(order.getDiscount() == null ? 0d : order.getDiscount())
            .receiverName(order.getReceiverName())
            .receiverPhone(order.getReceiverPhone())
            .receiverEmail(order.getReceiverEmail())
            .shippingAddress(order.getShippingAddress())
            .paymentMethod(order.getPaymentMethod())
            .paymentStatus(order.getPaymentStatus())
            .paymentProvider(order.getPaymentProvider())
            .paymentTxnRef(order.getPaymentTxnRef())
            .paymentTransactionId(order.getPaymentTransactionId())
            .paymentMessage(order.getPaymentMessage())
            .paidAt(order.getPaidAt())
            .status(order.getStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .items((order.getItems() == null ? Collections.<Order.OrderItem>emptyList() : order.getItems()).stream().map(item ->
                OrderItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .build()
            ).collect(Collectors.toList()))
            .build();
    }
}
