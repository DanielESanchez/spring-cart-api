package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${api.request.path}")
@RequiredArgsConstructor
public class ReviewController {

    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    private final ReviewService reviewService;
    @PostMapping("/review/new")
    ResponseEntity<?> saveReview(@Valid @RequestBody Review review) {
        Review reviewSaved = reviewService.saveReview(review);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", PROPERTY_NAME + "/review/get/" + reviewSaved.get_id());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @GetMapping("review/get/{reviewId}")
    Review getReview(@PathVariable String reviewId){
        return reviewService.findReviewById(reviewId);
    }

    @PutMapping("review/update")
    ResponseEntity<?> updateReview(@Valid @RequestBody Review review){
        reviewService.updateReview(review);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("review/delete/{reviewId}")
    ResponseEntity<?> deleteReview(@PathVariable String reviewId){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("reviews/get/{productId}")
    List<Review> getReviewsByProductId(@PathVariable String productId){
        return reviewService.findReviewsByProductId(productId);
    }
}
