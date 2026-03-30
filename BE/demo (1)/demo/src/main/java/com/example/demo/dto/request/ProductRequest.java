package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String name;
    
    private String author;
    private String imageUrl;
    
    @NotBlank(message = "Danh mục là bắt buộc")
    private String categoryId;
    
    private String status;
    
    @PositiveOrZero(message = "Giá tiền không được nhỏ hơn 0")
    private double price;
    
    @PositiveOrZero(message = "Số lượng không được nhỏ hơn 0")
    private int quantity;
    
    private String description;
}
