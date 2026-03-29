package com.example.demo.service.impl;

import com.example.demo.dto.request.AdminCreateUserRequest;
import com.example.demo.dto.request.AdminUpdateUserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AdminUserService;
import com.example.demo.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.from(user);
    }

    @Override
    public UserResponse createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .avatar(request.getAvatar())
            .role(request.getRole() == null ? User.Role.USER : request.getRole())
            .enabled(request.getEnabled() == null || request.getEnabled())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(String id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!AppUtil.isNullOrEmpty(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (!AppUtil.isNullOrEmpty(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (!AppUtil.isNullOrEmpty(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

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

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
