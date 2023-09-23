package com.springapi.springapitechnicaltest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.models.Review;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
import com.springapi.springapitechnicaltest.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class ReviewControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    ReviewService reviewService;
    Review reviewTest1;
    Review reviewTest2;
    List<Review> reviewList;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        reviewTest1 = new Review(5,"user","product");
        reviewTest1.set_id("1");
        reviewTest2 = new Review(4,"user","product2");
        reviewTest2.set_id("2");
        reviewList = Arrays.asList(
                reviewTest1, reviewTest2
        );
    }

    @Test
    @WithMockUser
    void saveReview() throws Exception {
        when(reviewService.saveReview(eq(reviewTest1)))
                .thenReturn(reviewTest1);

        mockMvc.perform(post("/api/v1/review/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewTest1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/review/get/1"));

        verify(reviewService, times(1)).saveReview(eq(reviewTest1));
    }

    @Test
    void saveReviewNoAuth() throws Exception {
        when(reviewService.saveReview(eq(reviewTest1)))
                .thenReturn(reviewTest1);

        mockMvc.perform(post("/api/v1/review/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewTest1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getReview() throws Exception {
        String reviewId = "1";
        when(reviewService.findReviewById(eq(reviewId)))
                .thenReturn(reviewTest1);

        mockMvc.perform(get("/api/v1/review/get/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._id", is(reviewId)))
                .andExpect(jsonPath("$.username", is("user")));

        verify(reviewService, times(1)).findReviewById(eq(reviewId));
    }

    @Test
    void getReviewNoAuth() throws Exception {
        String reviewId = "1";
        when(reviewService.findReviewById(eq(reviewId)))
                .thenReturn(reviewTest1);

        mockMvc.perform(get("/api/v1/review/get/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateReview() throws Exception {
        doNothing().when(reviewService).updateReview(eq(reviewTest1));

        mockMvc.perform(put("/api/v1/review/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewTest1)))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).updateReview(eq(reviewTest1));
    }

    @Test
    void updateReviewNoAuth() throws Exception {
        doNothing().when(reviewService).updateReview(eq(reviewTest1));

        mockMvc.perform(put("/api/v1/review/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewTest1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteReview() throws Exception {
        String reviewId = "1";
        doNothing().when(reviewService).deleteReview(eq(reviewId));

        mockMvc.perform(delete("/api/v1/review/delete/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewTest1)))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(eq(reviewId));
    }

    @Test
    void deleteReviewNoAuth() throws Exception {
        String reviewId = "1";
        doNothing().when(reviewService).deleteReview(eq(reviewId));

        mockMvc.perform(delete("/api/v1/review/delete/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewTest1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getReviewsByProductId() throws Exception {
        String productId = "123";

        when(reviewService.findReviewsByProductId(eq(productId)))
                .thenReturn(reviewList);

        mockMvc.perform(get("/api/v1/reviews/product/get/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].productId", is("product")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].productId", is("product2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(reviewService, times(1)).findReviewsByProductId(eq(productId));
    }

    private static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}