package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Review;

import java.util.List;

public interface ReviewService {
    Review saveReview(Review review);
    Review findReviewById(String reviewId);
    void updateReview(Review review);
    void deleteReview(String reviewId);
    List<Review> findReviewsByProductId(String productId);
}
