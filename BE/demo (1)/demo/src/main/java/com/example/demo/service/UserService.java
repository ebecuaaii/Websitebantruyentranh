package com.example.demo.service;

import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser();
    UserResponse updateProfile(UpdateProfileRequest request);
}
