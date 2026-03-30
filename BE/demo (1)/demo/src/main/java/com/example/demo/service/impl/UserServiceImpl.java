package com.example.demo.service.impl;

import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return UserResponse.from(user);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();

        if (!AppUtil.isNullOrEmpty(request.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (!AppUtil.isNullOrEmpty(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (!AppUtil.isNullOrEmpty(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return UserResponse.from(userRepository.save(user));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
