package com.example.demo.controller;

import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public BaseResponse<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return BaseResponse.success(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<CategoryResponse> update(@PathVariable String id, @RequestBody CategoryRequest request) {
        return BaseResponse.success(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return BaseResponse.success(null);
    }
}
