package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public BaseResponse<List<CategoryResponse>> getAll() {
        return BaseResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public BaseResponse<CategoryResponse> getById(@PathVariable String id) {
        return BaseResponse.success(categoryService.getCategoryById(id));
    }

    @GetMapping("/{id}/products")
    public BaseResponse<List<ProductResponse>> getProductsByCategory(@PathVariable String id) {
        return BaseResponse.success(productService.getProductsByCategory(id));
    }
}
