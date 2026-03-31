package com.example.demo.repository;

import com.example.demo.entity.News;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends MongoRepository<News, Long> {
    Optional<News> findBySlug(String slug);
    Optional<News> findTopByOrderByIdDesc();
}
