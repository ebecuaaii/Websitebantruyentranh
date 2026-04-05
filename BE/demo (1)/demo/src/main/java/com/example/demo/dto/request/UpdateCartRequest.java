package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartRequest {
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
