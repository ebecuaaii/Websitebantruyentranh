package com.example.demo.controller;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public BaseResponse<List<CategoryResponse>> getAll() {
        return BaseResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public BaseResponse<CategoryResponse> getById(@PathVariable String id) {
        return BaseResponse.success(categoryService.getCategoryById(id));
    }
}
