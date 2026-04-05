package com.example.demo.controller;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "2.4 Coupon – Phúc")
public class CouponController {

    private final CouponService couponService;

    // ── Admin ─────────────────────────────────────────────
    @Operation(summary = "Danh sách coupon", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/api/admin/coupons")
    public ResponseEntity<BaseResponse<?>> getAll() {
        return ResponseEntity.ok(BaseResponse.success(couponService.getAll()));
    }

    @Operation(summary = "Tạo coupon", security = @SecurityRequirement(name = "Bearer Auth"))
    @PostMapping("/api/admin/coupons")
    public ResponseEntity<BaseResponse<?>> create(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(BaseResponse.success(couponService.create(request)));
    }

    @Operation(summary = "Cập nhật coupon", security = @SecurityRequirement(name = "Bearer Auth"))
    @PutMapping("/api/admin/coupons/{id}")
    public ResponseEntity<BaseResponse<?>> update(@PathVariable String id,
                                                   @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(BaseResponse.success(couponService.update(id, request)));
    }

    @Operation(summary = "Xóa coupon", security = @SecurityRequirement(name = "Bearer Auth"))
    @DeleteMapping("/api/admin/coupons/{id}")
    public ResponseEntity<BaseResponse<?>> delete(@PathVariable String id) {
        couponService.delete(id);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    // ── Public ────────────────────────────────────────────
    @Operation(summary = "Kiểm tra mã coupon (public)")
    @GetMapping("/api/coupons/{code}")
    public ResponseEntity<BaseResponse<?>> validate(@PathVariable String code,
                                                     @RequestParam(defaultValue = "0") Double orderAmount) {
        return ResponseEntity.ok(BaseResponse.success(couponService.validate(code, orderAmount)));
    }
}
