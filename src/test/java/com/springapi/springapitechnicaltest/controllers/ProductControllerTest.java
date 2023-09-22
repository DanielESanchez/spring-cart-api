package com.springapi.springapitechnicaltest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
import com.springapi.springapitechnicaltest.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    ProductService productService;

    Product testProduct1;
    Product testProduct2;
    List<Product> productList = new ArrayList<>();
    String productId = "1";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        Set<String> categories = new HashSet<>();
        categories.add("cat1");
        testProduct1 = new Product("1","product1","product 1",categories,(float)20.5,20);
        testProduct2 = new Product("2","product2","product 2",categories,(float)21.5,5);
        productList.add(testProduct1);
        productList.add(testProduct2);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void saveProduct() throws Exception{
        when(productService.saveProduct(eq(testProduct1)))
                .thenReturn(testProduct1);

        mockMvc.perform(post("/api/v1/product/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testProduct1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/product/get/1"));

        verify(productService, times(1)).saveProduct(eq(testProduct1));
    }

    @Test
    @WithMockUser
    void saveProductUserRole() throws Exception{
        when(productService.saveProduct(eq(testProduct1)))
                .thenReturn(testProduct1);

        mockMvc.perform(post("/api/v1/product/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testProduct1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveProductNoAuthUserRole() throws Exception{
        when(productService.saveProduct(eq(testProduct1)))
                .thenReturn(testProduct1);

        mockMvc.perform(post("/api/v1/product/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testProduct1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getProduct() throws Exception {
        when(productService.getProductByProductId(eq(productId)))
                .thenReturn(testProduct1);

        mockMvc.perform(get("/api/v1/product/get/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(productId)))
                .andExpect(jsonPath("$.name", is("product1")));

        verify(productService, times(1)).getProductByProductId(eq(productId));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(eq(productId));

        mockMvc.perform(delete("/api/v1/product/delete/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(eq(productId));
    }

    @Test
    @WithMockUser
    void deleteProductRoleUser() throws Exception {
        doNothing().when(productService).deleteProduct(eq(productId));

        mockMvc.perform(delete("/api/v1/product/delete/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProductNoAuth() throws Exception {
        doNothing().when(productService).deleteProduct(eq(productId));

        mockMvc.perform(delete("/api/v1/product/delete/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateProduct() throws Exception{
        doNothing().when(productService).updateProduct(eq(testProduct1));

        mockMvc.perform(put("/api/v1/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testProduct1)))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).updateProduct(eq(testProduct1));
    }

    @Test
    @WithMockUser
    void updateProductRoleUser() throws Exception{
        doNothing().when(productService).updateProduct(eq(testProduct1));

        mockMvc.perform(put("/api/v1/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testProduct1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProductNoAuth() throws Exception{
        doNothing().when(productService).updateProduct(eq(testProduct1));

        mockMvc.perform(put("/api/v1/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testProduct1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllProducts() throws Exception {
        when(productService.findAll()).thenReturn(productList);

        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId", is("1")))
                .andExpect(jsonPath("$[0].name", is("product1")))
                .andExpect(jsonPath("$[0].price", is(20.5)))
                .andExpect(jsonPath("$[1].productId", is("2")))
                .andExpect(jsonPath("$[1].name", is("product2")))
                .andExpect(jsonPath("$[1].price", is(21.5)));

        verify(productService, times(1)).findAll();
    }

    @Test
    void getProductsByCategoryId() throws Exception {
        when(productService.getProductsByCategory("cat1")).thenReturn(productList);

        mockMvc.perform(get("/api/v1/products/category/{categoryId}", "cat1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId", is("1")))
                .andExpect(jsonPath("$[0].name", is("product1")))
                .andExpect(jsonPath("$[0].price", is(20.5)))
                .andExpect(jsonPath("$[1].productId", is("2")))
                .andExpect(jsonPath("$[1].name", is("product2")))
                .andExpect(jsonPath("$[1].price", is(21.5)));

        verify(productService, times(1)).getProductsByCategory(eq("cat1"));
    }

    @Test
    void searchProduct() throws Exception {
        String query = "search";
        Optional<String> category = Optional.of("cat1");

        when(productService.searchProduct(eq(query), eq(category)))
                .thenReturn(productList);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("q", query)
                        .param("category", category.get())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("product1")))
                .andExpect(jsonPath("$[1].name", is("product2")));

        verify(productService, times(1)).searchProduct(eq(query), eq(category));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void disableProduct() throws Exception {
        doNothing().when(productService).disableProduct(eq("1"));

        mockMvc.perform(patch("/api/v1/product/disable/{productId}", "1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).disableProduct(eq("1"));
    }

    @Test
    @WithMockUser
    void disableProductRoleUser() throws Exception {
        doNothing().when(productService).disableProduct(eq("1"));

        mockMvc.perform(patch("/api/v1/product/disable/{productId}", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void disableProductNoAuth() throws Exception {
        doNothing().when(productService).disableProduct(eq("1"));

        mockMvc.perform(patch("/api/v1/product/disable/{productId}", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void enableProduct() throws Exception {
        doNothing().when(productService).enableProduct(eq("1"));

        mockMvc.perform(patch("/api/v1/product/enable/{productId}", "1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).enableProduct(eq("1"));
    }

    @Test
    @WithMockUser
    void enableProductRoleUser() throws Exception {
        doNothing().when(productService).enableProduct(eq("1"));

        mockMvc.perform(patch("/api/v1/product/enable/{productId}", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void enableProductNoAuth() throws Exception {
        doNothing().when(productService).enableProduct(eq("1"));

        mockMvc.perform(patch("/api/v1/product/enable/{productId}", "1"))
                .andExpect(status().isForbidden());
    }

    private static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}