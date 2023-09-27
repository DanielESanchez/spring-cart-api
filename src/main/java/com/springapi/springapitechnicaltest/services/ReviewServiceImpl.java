package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.BadRequestException;
import com.springapi.springapitechnicaltest.controllers.ForbiddenException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;
    @Override
    public Review saveReview(Review review, String header) {
        productService.getProductByProductId(review.getProductId());
        //userService.findUserByUsername(review.getUsername());
        User userFound = userService.checkUser(header, review.getUsername());
        review.setUsername(userFound.getUsername());
        return reviewRepository.save(review);
    }

    @Override
    public Review findReviewById(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with id '"+ reviewId + "' could not be found"));
    }

    @Override
    public void updateReview(Review newReview, String header) {
        productService.getProductByProductId(newReview.getProductId());
        if(newReview.get_id().isEmpty() ) throw new BadRequestException("There is no id present for the review to update");
        Review reviewFound = findReviewById(newReview.get_id());
        userService.checkUser(header, reviewFound.getUsername());
        newReview.setUsername(reviewFound.getUsername());
        reviewRepository.save(newReview);
    }

    @Override
    public void deleteReview(String reviewId, String header) {
        Review reviewFound = findReviewById(reviewId);
        userService.checkUser(header, reviewFound.getUsername());
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<Review> findReviewsByProductId(String productId) {
        productService.getProductByProductId(productId);
        return reviewRepository.findReviewsByProductId(productId);
    }


}
