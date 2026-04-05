package com.example.demo.service.impl;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponse checkout(String userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Giỏ hàng trống"));
        if (cart.getItems() == null || cart.getItems().isEmpty())
            throw new RuntimeException("Giỏ hàng trống");

        // Build order items
        List<Order.OrderItem> items = cart.getItems().stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + cartItem.getProductId()));
            return Order.OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(cartItem.getQuantity())
                .price(product.getPrice())
                .build();
        }).collect(Collectors.toList());

        double total = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        double discount = 0;
        String couponCode = null;

        // Apply coupon
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Mã coupon không hợp lệ"));
            if (coupon.getDiscountPercent() != null)
                discount = total * coupon.getDiscountPercent() / 100;
            else if (coupon.getDiscountAmount() != null)
                discount = coupon.getDiscountAmount();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
            couponCode = coupon.getCode();
        }

        Order order = Order.builder()
            .userId(userId)
            .items(items)
            .totalAmount(total - discount)
            .discount(discount)
            .couponCode(couponCode)
            .shippingAddress(request.getShippingAddress())
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Order saved = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderResponse.from(saved);
    }

    @Override
    public Coupon validateCoupon(String code, Double orderAmount) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Mã coupon không hợp lệ"));
        if (!coupon.isActive()) throw new RuntimeException("Mã coupon đã bị vô hiệu hóa");
        if (coupon.getMinOrderAmount() != null && orderAmount < coupon.getMinOrderAmount())
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu");
        return coupon;
    }
}
