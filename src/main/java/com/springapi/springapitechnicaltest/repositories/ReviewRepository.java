package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    @Query(value = "{productId: '?0'}")
    List<Review> findReviewsByProductId(String productId);
}
