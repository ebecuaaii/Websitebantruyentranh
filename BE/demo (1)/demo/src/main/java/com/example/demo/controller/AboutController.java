package com.example.demo.controller;

import com.example.demo.dto.response.AboutResponse;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.service.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/about")
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;

    @GetMapping
    public BaseResponse<AboutResponse> getAbout() {
        return BaseResponse.success(aboutService.getAboutPage());
    }
}
