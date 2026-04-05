package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "about_pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AboutPage {

    @Id
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
