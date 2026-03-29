package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@kaleidoscope.vn}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean exists = userRepository.existsByUsername(adminUsername)
            || userRepository.existsByEmail(adminEmail);
        if (exists) {
            return;
        }

        User admin = User.builder()
            .username(adminUsername)
            .email(adminEmail)
            .password(passwordEncoder.encode(adminPassword))
            .role(User.Role.ADMIN)
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        userRepository.save(admin);
        LOGGER.info("Seeded default admin account: {}", adminUsername);
    }
}
