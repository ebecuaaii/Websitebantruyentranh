package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    long countByCategoryId(String categoryId);

    @Query("{'$or':[{'name': {'$regex': ?0, '$options':'i'}}, {'author': {'$regex': ?0, '$options':'i'}}]}")
    List<Product> searchByKeyword(String keywordRegex);

    @Query("{'categoryId': ?0, '$or':[{'name': {'$regex': ?1, '$options':'i'}}, {'author': {'$regex': ?1, '$options':'i'}}]}")
    List<Product> searchByCategoryAndKeyword(String categoryId, String keywordRegex);
}
