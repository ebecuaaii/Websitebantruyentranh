package com.example.demo.dto.request;

import com.example.demo.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {
    private String username;
    private String email;
    private String password;
    private User.Role role;
    private Boolean enabled;

    private String fullName;
    private String phone;
    private String avatar;
}
