package com.example.demo.service;

import com.example.demo.dto.response.WishlistResponse;

public interface WishlistService {
    WishlistResponse getWishlist();
    void addToWishlist(String productId);
    void removeFromWishlist(String productId);
}
