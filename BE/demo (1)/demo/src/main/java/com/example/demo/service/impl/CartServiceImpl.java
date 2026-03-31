package com.example.demo.service.impl;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponse getCart() {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(user.getId()).items(new ArrayList<>()).build()));
        return convertToResponse(cart);
    }

    @Override
    public CartResponse addToCart(CartItemRequest request) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> Cart.builder().userId(user.getId()).items(new ArrayList<>()).build());

        List<CartItem> items = cart.getItems();
        boolean exists = false;
        for (CartItem item : items) {
            if (item.getProductId().equals(request.getProductId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                exists = true;
                break;
            }
        }
        if (!exists) {
            items.add(CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build());
        }

        return convertToResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateQuantity(String productId, int quantity) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        return convertToResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse removeItem(String productId) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        return convertToResponse(cartRepository.save(cart));
    }

    @Override
    public void clearCart() {
        User user = getAuthenticatedUser();
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.setItems(new ArrayList<>());
            cartRepository.save(cart);
        });
    }

    private CartResponse convertToResponse(Cart cart) {
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            
            return CartResponse.CartItemResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImageUrl(product.getImageUrl())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(product.getPrice() * item.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        double totalAmount = itemResponses.stream().mapToDouble(CartResponse.CartItemResponse::getSubtotal).sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
