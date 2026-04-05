package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public BaseResponse<List<ProductResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId) {
        return BaseResponse.success(productService.searchProducts(keyword, categoryId));
    }

    @GetMapping("/{id}")
    public BaseResponse<ProductResponse> getById(@PathVariable String id) {
        return BaseResponse.success(productService.getProductById(id));
    }

    @GetMapping("/{id}/reviews")
    public BaseResponse<List<com.example.demo.dto.response.ReviewResponse>> getReviews(@PathVariable String id) {
        return BaseResponse.success(productService.getReviews(id));
    }

    @PostMapping("/{id}/reviews")
    public BaseResponse<com.example.demo.dto.response.ReviewResponse> addReview(
            @PathVariable String id, 
            @jakarta.validation.Valid @RequestBody com.example.demo.dto.request.ReviewRequest request) {
        return BaseResponse.success(productService.addReview(id, request));
    }
}
