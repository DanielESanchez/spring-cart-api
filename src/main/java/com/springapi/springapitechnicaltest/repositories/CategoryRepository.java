package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    @Query(value = "{categoryId:'?0'}")
    Optional<Category> findCategoryByCategoryId(String categoryId);
}
