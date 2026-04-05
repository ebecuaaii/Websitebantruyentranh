package com.example.demo.service.impl;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CouponResponse;
import com.example.demo.entity.Coupon;
import com.example.demo.entity.CouponType;
import com.example.demo.repository.CouponRepository;
import com.example.demo.service.CouponService;
import com.example.demo.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public Coupon validateCouponForOrder(String couponCode, double subtotalAmount) {
        if (AppUtil.isNullOrEmpty(couponCode)) {
            return null;
        }

        Coupon coupon = couponRepository.findByCode(normalizeCode(couponCode))
            .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));

        validateCoupon(coupon, subtotalAmount);
        return coupon;
    }

    @Override
    public double calculateDiscountAmount(Coupon coupon, double subtotalAmount) {
        if (coupon == null || subtotalAmount <= 0) {
            return 0d;
        }

        double value = coupon.getValue() == null ? 0d : coupon.getValue();
        double discount;

        if (coupon.getType() == CouponType.PERCENT) {
            discount = subtotalAmount * (value / 100d);
            double maxDiscount = coupon.getMaxDiscount() == null ? 0d : coupon.getMaxDiscount();
            if (maxDiscount > 0) {
                discount = Math.min(discount, maxDiscount);
            }
        } else {
            discount = value;
        }

        return Math.max(0d, Math.min(discount, subtotalAmount));
    }

    @Override
    public void increaseUsage(String couponCode) {
        if (AppUtil.isNullOrEmpty(couponCode)) {
            return;
        }

        Coupon coupon = couponRepository.findByCode(normalizeCode(couponCode))
            .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));

        Integer used = coupon.getUsedCount() == null ? 0 : coupon.getUsedCount();
        coupon.setUsedCount(used + 1);
        coupon.setUpdatedAt(LocalDateTime.now());
        couponRepository.save(coupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
            .map(CouponResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponse> getActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findAll().stream()
            .filter(coupon -> Boolean.TRUE.equals(coupon.getActive()))
            .filter(coupon -> coupon.getStartAt() == null || !now.isBefore(coupon.getStartAt()))
            .filter(coupon -> coupon.getEndAt() == null || !now.isAfter(coupon.getEndAt()))
            .filter(coupon -> {
                Integer limit = coupon.getUsageLimit() == null ? 0 : coupon.getUsageLimit();
                Integer used = coupon.getUsedCount() == null ? 0 : coupon.getUsedCount();
                return limit <= 0 || used < limit;
            })
            .map(CouponResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getCouponById(String id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));
        return CouponResponse.from(coupon);
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        validateCouponRequest(request);
        String code = normalizeCode(request.getCode());
        if (couponRepository.existsByCode(code)) {
            throw new RuntimeException("Coupon đã tồn tại");
        }

        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = Coupon.builder()
            .code(code)
            .type(request.getType())
            .value(request.getValue())
            .minOrderAmount(defaultDouble(request.getMinOrderAmount()))
            .maxDiscount(defaultDouble(request.getMaxDiscount()))
            .usageLimit(defaultInteger(request.getUsageLimit()))
            .usedCount(0)
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .active(request.getActive() == null || request.getActive())
            .createdAt(now)
            .updatedAt(now)
            .build();

        return CouponResponse.from(couponRepository.save(coupon));
    }

    @Override
    public CouponResponse updateCoupon(String id, CouponRequest request) {
        validateCouponRequest(request);
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));

        String code = normalizeCode(request.getCode());
        if (!coupon.getCode().equals(code) && couponRepository.existsByCode(code)) {
            throw new RuntimeException("Coupon đã tồn tại");
        }

        coupon.setCode(code);
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setMinOrderAmount(defaultDouble(request.getMinOrderAmount()));
        coupon.setMaxDiscount(defaultDouble(request.getMaxDiscount()));
        coupon.setUsageLimit(defaultInteger(request.getUsageLimit()));
        coupon.setStartAt(request.getStartAt());
        coupon.setEndAt(request.getEndAt());
        coupon.setActive(request.getActive() == null || request.getActive());
        coupon.setUpdatedAt(LocalDateTime.now());

        return CouponResponse.from(couponRepository.save(coupon));
    }

    @Override
    public void deleteCoupon(String id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Coupon không tồn tại");
        }
        couponRepository.deleteById(id);
    }

    private void validateCoupon(Coupon coupon, double subtotalAmount) {
        if (coupon == null) {
            throw new RuntimeException("Coupon không hợp lệ");
        }
        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new RuntimeException("Coupon đang bị khóa");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
            throw new RuntimeException("Coupon chưa đến thời gian áp dụng");
        }
        if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
            throw new RuntimeException("Coupon đã hết hạn");
        }

        Integer usageLimit = coupon.getUsageLimit() == null ? 0 : coupon.getUsageLimit();
        Integer usedCount = coupon.getUsedCount() == null ? 0 : coupon.getUsedCount();
        if (usageLimit > 0 && usedCount >= usageLimit) {
            throw new RuntimeException("Coupon đã hết lượt sử dụng");
        }

        double minOrder = coupon.getMinOrderAmount() == null ? 0d : coupon.getMinOrderAmount();
        if (subtotalAmount < minOrder) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để dùng coupon");
        }
    }

    private void validateCouponRequest(CouponRequest request) {
        if (request.getStartAt() != null && request.getEndAt() != null
            && request.getEndAt().isBefore(request.getStartAt())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        if (request.getType() == CouponType.PERCENT && request.getValue() > 100d) {
            throw new RuntimeException("Coupon phần trăm không được vượt quá 100%");
        }

        if (request.getUsageLimit() != null && request.getUsageLimit() < 0) {
            throw new RuntimeException("Số lượt sử dụng phải >= 0");
        }
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private double defaultDouble(Double value) {
        return value == null ? 0d : value;
    }

    private int defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }
}
