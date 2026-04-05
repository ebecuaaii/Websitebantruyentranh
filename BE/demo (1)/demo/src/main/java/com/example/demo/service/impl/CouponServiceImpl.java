package com.example.demo.service.impl;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.entity.Coupon;
import com.example.demo.repository.CouponRepository;
import com.example.demo.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getById(String id) {
        return couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public Coupon create(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode()))
            throw new RuntimeException("Mã coupon đã tồn tại");
        return couponRepository.save(Coupon.builder()
            .code(request.getCode().toUpperCase())
            .description(request.getDescription())
            .discountPercent(request.getDiscountPercent())
            .discountAmount(request.getDiscountAmount())
            .minOrderAmount(request.getMinOrderAmount())
            .maxUsage(request.getMaxUsage())
            .active(request.isActive())
            .expiresAt(request.getExpiresAt())
            .build());
    }

    @Override
    public Coupon update(String id, CouponRequest request) {
        Coupon coupon = getById(id);
        coupon.setDescription(request.getDescription());
        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setDiscountAmount(request.getDiscountAmount());
        coupon.setMinOrderAmount(request.getMinOrderAmount());
        coupon.setMaxUsage(request.getMaxUsage());
        coupon.setActive(request.isActive());
        coupon.setExpiresAt(request.getExpiresAt());
        return couponRepository.save(coupon);
    }

    @Override
    public void delete(String id) {
        couponRepository.deleteById(id);
    }

    @Override
    public Coupon validate(String code, Double orderAmount) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Mã coupon không hợp lệ"));
        if (!coupon.isActive()) throw new RuntimeException("Mã coupon đã bị vô hiệu hóa");
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Mã coupon đã hết hạn");
        if (coupon.getMaxUsage() != null && coupon.getUsedCount() >= coupon.getMaxUsage())
            throw new RuntimeException("Mã coupon đã hết lượt sử dụng");
        if (coupon.getMinOrderAmount() != null && orderAmount < coupon.getMinOrderAmount())
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderAmount().longValue() + " đ");
        return coupon;
    }
}
