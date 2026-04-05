package com.example.demo.repository;

import com.example.demo.entity.AboutPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AboutPageRepository extends MongoRepository<AboutPage, String> {
    Optional<AboutPage> findTopByOrderByUpdatedAtDesc();
}
