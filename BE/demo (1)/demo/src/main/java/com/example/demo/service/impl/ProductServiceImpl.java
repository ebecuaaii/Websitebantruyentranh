package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> ProductResponse.from(product, getCategoryName(product.getCategoryId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String categoryId) {
        String categoryName = getCategoryName(categoryId);
        return productRepository.findByCategoryId(categoryId).stream()
                .map(product -> ProductResponse.from(product, categoryName))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return ProductResponse.from(product, getCategoryName(product.getCategoryId()));
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .author(request.getAuthor())
                .imageUrl(request.getImageUrl())
                .categoryId(request.getCategoryId())
                .status(request.getStatus())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Product savedProduct = productRepository.save(product);
        updateCategoryProductCount(request.getCategoryId());
        
        return ProductResponse.from(savedProduct, getCategoryName(savedProduct.getCategoryId()));
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        
        String oldCategoryId = product.getCategoryId();
        
        product.setName(request.getName());
        product.setAuthor(request.getAuthor());
        product.setImageUrl(request.getImageUrl());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(request.getStatus());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        
        if (!oldCategoryId.equals(request.getCategoryId())) {
            updateCategoryProductCount(oldCategoryId);
            updateCategoryProductCount(request.getCategoryId());
        }
        
        return ProductResponse.from(updatedProduct, getCategoryName(updatedProduct.getCategoryId()));
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        
        String categoryId = product.getCategoryId();
        productRepository.deleteById(id);
        
        // Update product count in category
        updateCategoryProductCount(categoryId);
    }
    
    @Override
    public List<ReviewResponse> getReviews(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse addReview(String productId, ReviewRequest request) {
        // Kiểm tra xem sản phẩm có tồn tại không
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        UserResponse currentUser = userService.getCurrentUser();
        
        Review review = Review.builder()
                .productId(productId)
                .userId(currentUser.getId())
                .username(currentUser.getFullName())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        
        return ReviewResponse.from(reviewRepository.save(review));
    }
    
    private void updateCategoryProductCount(String categoryId) {
        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(category -> {
                int count = (int) productRepository.countByCategoryId(categoryId);
                category.setProductCount(count);
                categoryRepository.save(category);
            });
        }
    }

    private String getCategoryName(String categoryId) {
        if (categoryId == null) return "N/A";
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Unknown");
    }
}
