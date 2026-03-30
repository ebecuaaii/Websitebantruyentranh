package com.example.demo.controller;

import com.example.demo.dto.request.AdminCreateUserRequest;
import com.example.demo.dto.request.AdminUpdateUserRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public BaseResponse<List<UserResponse>> listUsers() {
        return BaseResponse.success(adminUserService.getAllUsers());
    }

    @GetMapping("/{id}")
    public BaseResponse<UserResponse> getUser(@PathVariable String id) {
        return BaseResponse.success(adminUserService.getUserById(id));
    }

    @PostMapping
    public BaseResponse<UserResponse> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return BaseResponse.success(adminUserService.createUser(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<UserResponse> updateUser(@PathVariable String id, @RequestBody AdminUpdateUserRequest request) {
        return BaseResponse.success(adminUserService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable String id) {
        adminUserService.deleteUser(id);
        return BaseResponse.success(null);
    }
}
