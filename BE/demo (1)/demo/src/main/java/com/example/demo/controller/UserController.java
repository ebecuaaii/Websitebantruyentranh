package com.example.demo.controller;

import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public BaseResponse<UserResponse> me() {
        return BaseResponse.success(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public BaseResponse<UserResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return BaseResponse.success(userService.updateProfile(request));
    }
}
