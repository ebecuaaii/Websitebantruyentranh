package com.example.demo.service.impl;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WishlistRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public WishlistResponse getWishlist() {
        User user = getAuthenticatedUser();
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder().userId(user.getId()).productIds(new HashSet<>()).build()));

        List<ProductResponse> products = wishlist.getProductIds().stream()
                .map(id -> {
                    Product product = productRepository.findById(id).orElse(null);
                    if (product == null) return null;
                    String categoryName = categoryRepository.findById(product.getCategoryId())
                            .map(c -> c.getName()).orElse("Unknown");
                    return ProductResponse.from(product, categoryName);
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

        return WishlistResponse.builder().products(products).build();
    }

    @Override
    public void addToWishlist(String productId) {
        User user = getAuthenticatedUser();
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> Wishlist.builder().userId(user.getId()).productIds(new HashSet<>()).build());

        wishlist.getProductIds().add(productId);
        wishlistRepository.save(wishlist);
    }

    @Override
    public void removeFromWishlist(String productId) {
        User user = getAuthenticatedUser();
        wishlistRepository.findByUserId(user.getId()).ifPresent(wishlist -> {
            wishlist.getProductIds().remove(productId);
            wishlistRepository.save(wishlist);
        });
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
