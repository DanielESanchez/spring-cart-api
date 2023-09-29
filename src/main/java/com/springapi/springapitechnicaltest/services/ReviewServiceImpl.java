package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.BadRequestException;
import com.springapi.springapitechnicaltest.controllers.ForbiddenException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;
    @Override
    public Review saveReview(Review review, String header) {
        productService.getProductByProductId(review.getProductId());
        User userFound = userService.checkUser(header, review.getUsername());
        review.setUsername(userFound.getUsername());
        log.info(new Date() + " The review for the product "
                + review.getProductId() + " has been updated by user " + userFound.getUsername() );
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
        log.info(new Date() + " The review for the product "
                + reviewFound.getProductId() + " has been updated by user " + reviewFound.getUsername() );
    }

    @Override
    public void deleteReview(String reviewId, String header) {
        Review reviewFound = findReviewById(reviewId);
        userService.checkUser(header, reviewFound.getUsername());
        reviewRepository.deleteById(reviewId);
        log.info(new Date() + " The review for the product "
                + reviewFound.getProductId() + " has been updated by user " + reviewFound.getUsername() );
    }

    @Override
    public List<Review> findReviewsByProductId(String productId) {
        productService.getProductByProductId(productId);
        return reviewRepository.findReviewsByProductId(productId);
    }


}
