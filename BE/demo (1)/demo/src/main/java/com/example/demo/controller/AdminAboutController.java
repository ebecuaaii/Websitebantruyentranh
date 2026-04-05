package com.example.demo.controller;

import com.example.demo.dto.request.AboutRequest;
import com.example.demo.dto.response.AboutResponse;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.service.AboutService;
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
@RequestMapping("/api/admin/about")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAboutController {

    private final AboutService aboutService;

    @GetMapping
    public BaseResponse<List<AboutResponse>> list() {
        return BaseResponse.success(aboutService.getAllAboutPages());
    }

    @PostMapping
    public BaseResponse<AboutResponse> create(@Valid @RequestBody AboutRequest request) {
        return BaseResponse.success(aboutService.create(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<AboutResponse> update(@PathVariable String id, @Valid @RequestBody AboutRequest request) {
        return BaseResponse.success(aboutService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable String id) {
        aboutService.delete(id);
        return BaseResponse.success(null);
    }
}
