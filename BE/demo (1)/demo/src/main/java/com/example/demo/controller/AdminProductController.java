package com.example.demo.controller;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public BaseResponse<ProductResponse> create(@RequestBody ProductRequest request) {
        return BaseResponse.success(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<ProductResponse> update(@PathVariable String id, @RequestBody ProductRequest request) {
        return BaseResponse.success(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return BaseResponse.success(null);
    }
}
