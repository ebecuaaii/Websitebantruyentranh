package com.example.demo.service;

import com.example.demo.dto.request.AboutRequest;
import com.example.demo.dto.response.AboutResponse;

import java.util.List;

public interface AboutService {
    AboutResponse getAboutPage();
    List<AboutResponse> getAllAboutPages();
    AboutResponse create(AboutRequest request);
    AboutResponse update(String id, AboutRequest request);
    void delete(String id);
}
