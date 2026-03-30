package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Tên danh mục là bắt buộc")
    private String name;
    private String icon;
    private String thumbnailUrl;
}
