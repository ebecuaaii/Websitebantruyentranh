package com.example.demo.service.impl;

import com.example.demo.dto.request.NewsRequest;
import com.example.demo.dto.response.NewsResponse;
import com.example.demo.dto.response.NewsSummaryResponse;
import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public List<NewsSummaryResponse> getAllNews() {
        return newsRepository.findAll().stream()
            .sorted(Comparator.comparing(News::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .map(NewsSummaryResponse::from)
            .toList();
    }

    @Override
    public NewsResponse getById(Long id) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        return NewsResponse.from(news);
    }

    @Override
    public NewsResponse getBySlug(String slug) {
        News news = newsRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        return NewsResponse.from(news);
    }

    @Override
    public NewsResponse create(NewsRequest request) {
        newsRepository.findBySlug(request.getSlug()).ifPresent(existing -> {
            throw new RuntimeException("Slug đã tồn tại");
        });

        News news = mapFromRequest(request);
        news.setId(nextNewsId());
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());
        if (news.getPublishedAt() == null) {
            news.setPublishedAt(Instant.now());
        }

        return NewsResponse.from(newsRepository.save(news));
    }

    @Override
    public NewsResponse update(Long id, NewsRequest request) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        newsRepository.findBySlug(request.getSlug())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new RuntimeException("Slug đã tồn tại");
            });

        News updated = mapFromRequest(request);
        news.setTitle(updated.getTitle());
        news.setSlug(updated.getSlug());
        news.setThumbnail(updated.getThumbnail());
        news.setSummary(updated.getSummary());
        news.setContent(updated.getContent());
        news.setCategory(updated.getCategory());
        news.setAuthor(updated.getAuthor());
        news.setPublishedAt(updated.getPublishedAt() == null ? news.getPublishedAt() : updated.getPublishedAt());
        news.setRelatedBooks(updated.getRelatedBooks());
        news.setTags(updated.getTags());
        news.setUpdatedAt(LocalDateTime.now());

        return NewsResponse.from(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bài viết");
        }
        newsRepository.deleteById(id);
    }

    private Long nextNewsId() {
        return newsRepository.findTopByOrderByIdDesc()
            .map(item -> item.getId() + 1)
            .orElse(1L);
    }

    private News mapFromRequest(NewsRequest request) {
        return News.builder()
            .title(request.getTitle())
            .slug(request.getSlug())
            .thumbnail(request.getThumbnail())
            .summary(request.getSummary())
            .content(request.getContent())
            .category(News.NewsCategory.builder()
                .id(request.getCategory().getId())
                .name(request.getCategory().getName())
                .build())
            .author(News.NewsAuthor.builder()
                .name(request.getAuthor().getName())
                .avatar(request.getAuthor().getAvatar())
                .build())
            .publishedAt(request.getPublishedAt())
            .relatedBooks(request.getRelatedBooks() == null ? List.of() : request.getRelatedBooks().stream()
                .map(book -> News.NewsRelatedBook.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .price(book.getPrice())
                    .image(book.getImage())
                    .build())
                .toList())
            .tags(request.getTags() == null ? List.of() : request.getTags())
            .build();
    }
}
