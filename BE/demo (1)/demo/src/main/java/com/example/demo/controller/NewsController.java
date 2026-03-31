package com.example.demo.controller;

import com.example.demo.dto.response.NewsApiResponse;
import com.example.demo.dto.response.NewsResponse;
import com.example.demo.dto.response.NewsSummaryResponse;
import com.example.demo.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public NewsApiResponse<List<NewsSummaryResponse>> getAll() {
        return NewsApiResponse.success(newsService.getAllNews());
    }

    @GetMapping("/{id}")
    public NewsApiResponse<NewsResponse> getById(@PathVariable Long id) {
        return NewsApiResponse.success(newsService.getById(id));
    }

    @GetMapping("/slug/{slug}")
    public NewsApiResponse<NewsResponse> getBySlug(@PathVariable String slug) {
        return NewsApiResponse.success(newsService.getBySlug(slug));
    }
}
