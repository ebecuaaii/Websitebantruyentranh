package com.example.demo.service.impl;

import com.example.demo.dto.request.AboutRequest;
import com.example.demo.dto.response.AboutResponse;
import com.example.demo.entity.AboutPage;
import com.example.demo.repository.AboutPageRepository;
import com.example.demo.service.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutPageRepository aboutPageRepository;

    @Override
    public AboutResponse getAboutPage() {
        AboutPage page = aboutPageRepository.findTopByOrderByUpdatedAtDesc()
            .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu trang giới thiệu"));
        return AboutResponse.from(page);
    }

    @Override
    public List<AboutResponse> getAllAboutPages() {
        return aboutPageRepository.findAll().stream()
            .sorted(Comparator.comparing(AboutPage::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .map(AboutResponse::from)
            .toList();
    }

    @Override
    public AboutResponse create(AboutRequest request) {
        String id = request.getId();
        if (id == null || id.isBlank()) {
            id = "about-" + UUID.randomUUID().toString().substring(0, 8);
        } else if (aboutPageRepository.existsById(id)) {
            throw new RuntimeException("ID trang giới thiệu đã tồn tại");
        }

        AboutPage page = mapFromRequest(request);
        page.setId(id);
        page.setCreatedAt(LocalDateTime.now());
        page.setUpdatedAt(LocalDateTime.now());

        return AboutResponse.from(aboutPageRepository.save(page));
    }

    @Override
    public AboutResponse update(String id, AboutRequest request) {
        AboutPage page = aboutPageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu trang giới thiệu"));

        page.setTitle(request.getTitle());
        page.setTagline(request.getTagline());
        page.setStory(request.getStory());
        page.setMission(request.getMission());
        page.setVision(request.getVision());
        page.setEmail(request.getEmail());
        page.setHotline(request.getHotline());
        page.setAddress(request.getAddress());
        page.setHighlights(request.getHighlights() == null ? List.of() : request.getHighlights());
        page.setUpdatedAt(LocalDateTime.now());

        return AboutResponse.from(aboutPageRepository.save(page));
    }

    @Override
    public void delete(String id) {
        if (!aboutPageRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy dữ liệu trang giới thiệu");
        }
        aboutPageRepository.deleteById(id);
    }

    private AboutPage mapFromRequest(AboutRequest request) {
        return AboutPage.builder()
            .title(request.getTitle())
            .tagline(request.getTagline())
            .story(request.getStory())
            .mission(request.getMission())
            .vision(request.getVision())
            .email(request.getEmail())
            .hotline(request.getHotline())
            .address(request.getAddress())
            .highlights(request.getHighlights() == null ? List.of() : request.getHighlights())
            .build();
    }
}
