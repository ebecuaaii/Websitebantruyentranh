package com.example.demo.dto.request;

import com.example.demo.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotBlank(message = "Receiver name is required")
    private String receiverName;

    @NotBlank(message = "Receiver phone is required")
    private String receiverPhone;

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Receiver email is invalid")
    private String receiverEmail;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String shippingMethod;

    @DecimalMin(value = "0.0", message = "Shipping fee must be >= 0")
    private Double shippingFee = 0d;

    private String couponCode;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
