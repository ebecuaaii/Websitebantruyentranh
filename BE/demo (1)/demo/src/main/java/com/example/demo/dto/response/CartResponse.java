package com.example.demo.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartResponse {
    private String id;
    private List<CartItemResponse> items;
    private double totalAmount;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartItemResponse {
        private String productId;
        private String productName;
        private String productImageUrl;
        private double price;
        private int quantity;
        private double subtotal;
    }
}
