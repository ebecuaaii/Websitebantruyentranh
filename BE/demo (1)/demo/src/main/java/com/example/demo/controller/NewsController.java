package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
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
    public BaseResponse<List<NewsSummaryResponse>> getAll() {
        return BaseResponse.success(newsService.getAllNews());
    }

    @GetMapping("/{id}")
    public BaseResponse<NewsResponse> getById(@PathVariable Long id) {
        return BaseResponse.success(newsService.getById(id));
    }

    @GetMapping("/slug/{slug}")
    public BaseResponse<NewsResponse> getBySlug(@PathVariable String slug) {
        return BaseResponse.success(newsService.getBySlug(slug));
    }
}
