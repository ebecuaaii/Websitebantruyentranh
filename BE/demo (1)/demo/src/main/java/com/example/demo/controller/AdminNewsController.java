package com.example.demo.controller;

import com.example.demo.dto.request.NewsRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.NewsResponse;
import com.example.demo.dto.response.NewsSummaryResponse;
import com.example.demo.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping
    public BaseResponse<List<NewsSummaryResponse>> list() {
        return BaseResponse.success(newsService.getAllNews());
    }

    @PostMapping
    public BaseResponse<NewsResponse> create(@Valid @RequestBody NewsRequest request) {
        return BaseResponse.success(newsService.create(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<NewsResponse> update(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        return BaseResponse.success(newsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return BaseResponse.success(null);
    }
}
