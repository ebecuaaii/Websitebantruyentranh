package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public BaseResponse<WishlistResponse> getWishlist() {
        return BaseResponse.success(wishlistService.getWishlist());
    }

    @PostMapping("/{productId}")
    public BaseResponse<Void> addToWishlist(@PathVariable String productId) {
        wishlistService.addToWishlist(productId);
        return BaseResponse.success(null);
    }

    @DeleteMapping("/{productId}")
    public BaseResponse<Void> removeFromWishlist(@PathVariable String productId) {
        wishlistService.removeFromWishlist(productId);
        return BaseResponse.success(null);
    }
}
