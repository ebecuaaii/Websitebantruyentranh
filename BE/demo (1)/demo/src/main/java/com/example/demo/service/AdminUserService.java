package com.example.demo.service;

import com.example.demo.dto.request.AdminCreateUserRequest;
import com.example.demo.dto.request.AdminUpdateUserRequest;
import com.example.demo.dto.response.UserResponse;

import java.util.List;

public interface AdminUserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(String id);
    UserResponse createUser(AdminCreateUserRequest request);
    UserResponse updateUser(String id, AdminUpdateUserRequest request);
    void deleteUser(String id);
}
