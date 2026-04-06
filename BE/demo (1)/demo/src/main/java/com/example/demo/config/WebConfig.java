package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Cho phép tất cả các đường dẫn API (vd: /api/home, /api/products)
                .allowedOrigins(
                    "https://websitebantruyentranhv20.vercel.app", // Link Production của ông
                    "http://localhost:5500",                       // Link chạy Live Server ở máy local
                    "http://127.0.0.1:5500"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các phương thức được phép
                .allowedHeaders("*")            // Cho phép tất cả các Header
                .allowCredentials(true)  // Quan trọng: Cho phép gửi kèm Cookie/Auth Header
                .maxAge(3600);                    // Cache cấu hình CORS trong 1 tiếng để tăng tốc
    }
}