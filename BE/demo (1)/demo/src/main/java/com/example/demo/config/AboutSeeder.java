package com.example.demo.config;

import com.example.demo.entity.AboutPage;
import com.example.demo.repository.AboutPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AboutSeeder implements CommandLineRunner {

    private final AboutPageRepository aboutPageRepository;

    @Override
    public void run(String... args) {
        if (aboutPageRepository.existsById("main")) {
            return;
        }

        AboutPage about = AboutPage.builder()
            .id("main")
            .title("Về Chúng Tôi")
            .tagline("Kết nối người yêu sách và truyện tranh bằng trải nghiệm mua sắm hiện đại.")
            .story("Comic Store được xây dựng để giúp người đọc dễ dàng khám phá và sở hữu những tựa sách phù hợp.")
            .mission("Mang tri thức và cảm hứng đọc đến gần hơn với mọi người.")
            .vision("Trở thành nền tảng bán sách và truyện tranh trực tuyến được yêu thích tại Việt Nam.")
            .email("support@comicstore.vn")
            .hotline("1900 6868")
            .address("Quận 1, TP. Hồ Chí Minh")
            .highlights(List.of(
                "Sản phẩm được chọn lọc theo danh mục",
                "Đội ngũ hỗ trợ phản hồi nhanh",
                "Nội dung tin tức và review cập nhật hằng tuần"
            ))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        aboutPageRepository.save(about);
    }
}
