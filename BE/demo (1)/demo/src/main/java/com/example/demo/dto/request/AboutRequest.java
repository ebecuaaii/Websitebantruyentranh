package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AboutRequest {
    private String id;

    @NotBlank(message = "Title là bắt buộc")
    private String title;

    private String tagline;
    private String story;
    private String mission;
    private String vision;
    private String email;
    private String hotline;
    private String address;
    private List<String> highlights;
}
