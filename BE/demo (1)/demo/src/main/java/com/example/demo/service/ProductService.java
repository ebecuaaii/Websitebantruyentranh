package com.example.demo.service;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> searchProducts(String keyword, String categoryId);
    List<ProductResponse> getProductsByCategory(String categoryId);
    ProductResponse getProductById(String id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(String id, ProductRequest request);
    void deleteProduct(String id);
    
    // Reviews
    List<com.example.demo.dto.response.ReviewResponse> getReviews(String productId);
    com.example.demo.dto.response.ReviewResponse addReview(String productId, com.example.demo.dto.request.ReviewRequest request);
}
