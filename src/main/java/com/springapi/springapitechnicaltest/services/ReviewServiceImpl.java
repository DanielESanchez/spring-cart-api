package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.BadRequestException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.repositories.ReviewRepository;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    @Override
    public Review saveReview(Review review) {
        productService.getProductByProductId(review.getProductId());
        userRepository.findUserByUsername(review.getUsername())
                .orElseThrow(() -> new NotFoundException("The user '"+ review.getUsername() + "' could not be found"));
        return reviewRepository.save(review);
    }

    @Override
    public Review findReviewById(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with id '"+ reviewId + "' could not be found"));
    }

    @Override
    public void updateReview(Review newReview) {
        productService.getProductByProductId(newReview.getProductId());
        if(newReview.get_id().isEmpty() || newReview.get_id()==null ) throw new BadRequestException("There is no id present for the review to update");
        reviewRepository.save(newReview);
    }

    @Override
    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<Review> findReviewsByProductId(String productId) {
        productService.getProductByProductId(productId);
        return reviewRepository.findReviewsByProductId(productId);
    }
}