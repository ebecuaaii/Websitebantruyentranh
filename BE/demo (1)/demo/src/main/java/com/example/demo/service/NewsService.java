package com.example.demo.service;

import com.example.demo.dto.request.NewsRequest;
import com.example.demo.dto.response.NewsResponse;
import com.example.demo.dto.response.NewsSummaryResponse;

import java.util.List;

public interface NewsService {
    List<NewsSummaryResponse> getAllNews();
    NewsResponse getById(Long id);
    NewsResponse getBySlug(String slug);
    NewsResponse create(NewsRequest request);
    NewsResponse update(Long id, NewsRequest request);
    void delete(Long id);
}
