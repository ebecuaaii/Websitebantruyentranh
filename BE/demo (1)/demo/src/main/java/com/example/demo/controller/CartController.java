package com.example.demo.controller;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.UpdateCartRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public BaseResponse<CartResponse> getCart() {
        return BaseResponse.success(cartService.getCart());
    }

    @PostMapping
    public BaseResponse<CartResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        return BaseResponse.success(cartService.addToCart(request));
    }

    @PutMapping("/{productId}")
    public BaseResponse<CartResponse> updateQuantity(
            @PathVariable String productId, 
            @Valid @RequestBody UpdateCartRequest request) {
        return BaseResponse.success(cartService.updateQuantity(productId, request.getQuantity()));
    }

    @DeleteMapping("/{productId}")
    public BaseResponse<CartResponse> removeItem(@PathVariable String productId) {
        return BaseResponse.success(cartService.removeItem(productId));
    }

    @DeleteMapping
    public BaseResponse<Void> clearCart() {
        cartService.clearCart();
        return BaseResponse.success(null);
    }
}
