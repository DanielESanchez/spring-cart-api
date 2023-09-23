package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.BadRequestException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import com.springapi.springapitechnicaltest.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ReviewServiceTest {
    @Autowired
    private ReviewServiceImpl reviewService;

    @MockBean
    private ProductServiceImpl productService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    ReviewRepository reviewRepository;

    @MockBean
    ProductRepository productRepository;
    Review review;
    Review review2;
    Product product;
    String username = "user";
    List<Review> reviewsList;

    @BeforeEach
    public void setup(){
        review = new Review(5,"user","product1");
        review.set_id("0");
        review2 = new Review(5,"user","product2");
        review2.set_id("1");
        Set<String> categories = new HashSet<>();
        categories.add("cat1");
        product =  new Product("product1","product 1", "description 1", categories,(float) 15.5, 5);
        reviewsList = new ArrayList<>();
        reviewsList.add(review);
        reviewsList.add(review2);
    }

    @Test
    void saveReview() {
        when(productService.getProductByProductId(eq(product.getProductId()))).thenReturn(product);
        when(userService.findUserByUsername(eq(username))).thenReturn(new User());
        when(reviewRepository.save(eq(review))).thenReturn(review2);

        Review resultReview = reviewService.saveReview(review);

        verify(productService, times(1)).getProductByProductId(eq("product1"));
        verify(userService, times(1)).findUserByUsername(eq("user"));
        verify(reviewRepository, times(1)).save(eq(review));

        assertNotNull(resultReview);
    }

    @Test
    void findReviewById() {
        String reviewId = "1";

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review2));

        Review result = reviewService.findReviewById(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.get_id());
    }

    @Test
    void findReviewByIdNotFound() {
        String reviewId = "noFoundId";

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> reviewService.findReviewById(reviewId));
    }

    @Test
    void updateReview() {
        when(productService.getProductByProductId(review.getProductId()))
                .thenReturn(product);
        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review);

        reviewService.updateReview(review);

        verify(productService, times(1)).getProductByProductId(review.getProductId());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void updateReviewWithEmptyId() {
        Review invalidReview = review;
        review.set_id("");

        when(productService.getProductByProductId(invalidReview.getProductId()))
                .thenReturn(product);

        assertThrows(BadRequestException.class, ()-> reviewService.updateReview(invalidReview));

        verify(productService, times(1)).getProductByProductId(invalidReview.getProductId());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview() {
        doNothing().when(productService).deleteProduct(review.get_id());
        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review);

        reviewService.deleteReview(review.get_id());

        verify(reviewRepository, times(1)).deleteById(review.get_id());
    }

    @Test
    void findReviewsByProductId() {
        String productId = "1";

        when(productService.getProductByProductId(productId)).thenReturn(product);
        when(reviewRepository.findReviewsByProductId(productId)).thenReturn(reviewsList);

        List<Review> result = reviewService.findReviewsByProductId(productId);

        verify(productService, times(1)).getProductByProductId(productId);
        verify(reviewRepository, times(1)).findReviewsByProductId(productId);


        assertEquals(reviewsList, result);
    }
}