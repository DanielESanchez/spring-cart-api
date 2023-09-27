package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Review;

import java.util.List;

public interface ReviewService {
    Review saveReview(Review review, String header);
    Review findReviewById(String reviewId);
    void updateReview(Review review, String header);
    void deleteReview(String reviewId, String header);
    List<Review> findReviewsByProductId(String productId);
}
