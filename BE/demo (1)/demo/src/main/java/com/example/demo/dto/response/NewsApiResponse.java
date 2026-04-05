package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsApiResponse<T> {
    private String status;
    private T data;

    public static <T> NewsApiResponse<T> success(T data) {
        return NewsApiResponse.<T>builder()
            .status("success")
            .data(data)
            .build();
    }
}
