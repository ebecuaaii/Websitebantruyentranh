package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponse {
    private List<CategoryResponse> categories;
    private List<ProductResponse> featuredProducts;
    private List<NewsSummaryResponse> latestNews;
    private Instant generatedAt;
}
