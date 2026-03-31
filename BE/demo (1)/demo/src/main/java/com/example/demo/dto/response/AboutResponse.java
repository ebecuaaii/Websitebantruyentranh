package com.example.demo.dto.response;

import com.example.demo.entity.AboutPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AboutResponse {
    private String id;
    private String title;
    private String tagline;
    private String story;
    private String mission;
    private String vision;
    private String email;
    private String hotline;
    private String address;
    private List<String> highlights;

    public static AboutResponse from(AboutPage page) {
        return AboutResponse.builder()
            .id(page.getId())
            .title(page.getTitle())
            .tagline(page.getTagline())
            .story(page.getStory())
            .mission(page.getMission())
            .vision(page.getVision())
            .email(page.getEmail())
            .hotline(page.getHotline())
            .address(page.getAddress())
            .highlights(page.getHighlights())
            .build();
    }
}
