package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.services.ReviewService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
public class ReviewController {

    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    private final ReviewService reviewService;
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Review saved successfully", headers = {
                    @Header(name = HttpHeaders.LOCATION, schema =
                    @Schema(type = "string"),
                            description = "Path of the review saved") },
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Product or user not found",
                    content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/review/new")
    ResponseEntity<HttpHeaders> saveReview(@Valid @RequestBody Review review, @Schema(hidden = true) @RequestHeader("Authorization") String header) {
        Review reviewSaved = reviewService.saveReview(review, header);
        String location = PROPERTY_NAME + "/review/get/" + reviewSaved.get_id();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing the review with the id received",
                    content = { @Content(schema = @Schema(implementation = Review.class)) }),
            @ApiResponse(responseCode = "404", description = "Review not found", content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("review/get/{reviewId}")
    Review getReview(@PathVariable String reviewId){
        return reviewService.findReviewById(reviewId);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Review updated",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Review not found", content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("review/update")
    ResponseEntity<?> updateReview(@Valid @RequestBody Review review, @Schema(hidden = true) @RequestHeader("Authorization") String header){
        reviewService.updateReview(review, header);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Review deleted",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Review not found", content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("review/delete/{reviewId}")
    ResponseEntity<?> deleteReview(@PathVariable String reviewId, @Schema(hidden = true) @RequestHeader("Authorization") String header){
        reviewService.deleteReview(reviewId, header);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all reviews saved for the product with the id received"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("reviews/product/get/{productId}")
    List<Review> getReviewsByProductId(@PathVariable String productId){
        return reviewService.findReviewsByProductId(productId);
    }
}
