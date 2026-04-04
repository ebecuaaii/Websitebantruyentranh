package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.response.HomeResponse;
import com.example.demo.dto.response.NewsSummaryResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.CategoryService;
import com.example.demo.service.NewsService;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final NewsService newsService;

    @GetMapping
    public BaseResponse<HomeResponse> getHome(
            @RequestParam(defaultValue = "6") int categoryLimit,
            @RequestParam(defaultValue = "8") int productLimit,
            @RequestParam(defaultValue = "3") int newsLimit) {

        int safeCategoryLimit = normalizeLimit(categoryLimit, 6, 1, 20);
        int safeProductLimit = normalizeLimit(productLimit, 8, 1, 50);
        int safeNewsLimit = normalizeLimit(newsLimit, 3, 1, 20);

        List<CategoryResponse> categories = categoryService.getAllCategories().stream()
                .sorted(Comparator.comparingInt(CategoryResponse::getProductCount).reversed())
                .limit(safeCategoryLimit)
                .toList();

        List<ProductResponse> featuredProducts = productService.getAllProducts().stream()
                .sorted(Comparator.comparing(ProductResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(safeProductLimit)
                .toList();

        List<NewsSummaryResponse> latestNews = newsService.getAllNews().stream()
                .limit(safeNewsLimit)
                .toList();

        HomeResponse response = HomeResponse.builder()
                .categories(categories)
                .featuredProducts(featuredProducts)
                .latestNews(latestNews)
                .generatedAt(Instant.now())
                .build();

        return BaseResponse.success(response);
    }

    private int normalizeLimit(int value, int defaultValue, int min, int max) {
        if (value < min) {
            return defaultValue;
        }
        return Math.min(value, max);
    }
}
