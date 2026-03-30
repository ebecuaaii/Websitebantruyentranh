package com.example.demo.controller;

import com.example.demo.dto.request.BaseRequest;
import com.example.demo.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "2.1 Auth - Thuận", description = "Đăng ký, đăng nhập, tài khoản cá nhân")
public class AuthController {

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<?>> register(@RequestBody BaseRequest request) {
        return ResponseEntity.ok(BaseResponse.success("register - TODO"));
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(@RequestBody BaseRequest request) {
        return ResponseEntity.ok(BaseResponse.success("login - TODO"));
    }

    @Operation(summary = "Lấy thông tin tài khoản cá nhân", security = @SecurityRequirement(name = "Bearer Auth"))
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<?>> getMe() {
        return ResponseEntity.ok(BaseResponse.success("getMe - TODO"));
    }

    @Operation(summary = "Cập nhật thông tin cá nhân", security = @SecurityRequirement(name = "Bearer Auth"))
    @PutMapping("/me")
    public ResponseEntity<BaseResponse<?>> updateMe(@RequestBody BaseRequest request) {
        return ResponseEntity.ok(BaseResponse.success("updateMe - TODO"));
    }
}
