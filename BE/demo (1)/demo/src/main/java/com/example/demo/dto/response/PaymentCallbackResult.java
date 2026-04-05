package com.example.demo.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallbackResult {
    private String orderId;
    private String gateway;
    private boolean success;
    private String message;
}
