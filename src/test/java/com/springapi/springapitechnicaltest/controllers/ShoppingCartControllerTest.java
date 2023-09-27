package com.springapi.springapitechnicaltest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.models.ProductShoppingCart;
import com.springapi.springapitechnicaltest.models.ShoppingCart;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
import com.springapi.springapitechnicaltest.services.ShoppingCartService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class ShoppingCartControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    ShoppingCartService shoppingCartService;
    ShoppingCart shoppingCartTest;
    String username = "user";
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        shoppingCartTest = new ShoppingCart();
        shoppingCartTest.setTotal((float) 115.5);
        shoppingCartTest.setUsername(username);
        shoppingCartTest.set_id("1");
    }


    @Test
    @WithMockUser
    void shouldReturnShoppingCart_WhenGetShoppingCartWithUserRole() throws Exception {
        when(shoppingCartService.findShoppingCartByUsername(eq(username)))
                .thenReturn(shoppingCartTest);

        mockMvc.perform(get("/api/v1/cart/get/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.total", is(115.5)));

        verify(shoppingCartService, times(1)).findShoppingCartByUsername(eq(username));
    }

    @Test
    void shouldReturnForbidden_WhenGetShoppingCartWithNoAuth() throws Exception {
        when(shoppingCartService.findShoppingCartByUsername(eq(username)))
                .thenReturn(shoppingCartTest);

        mockMvc.perform(get("/api/v1/cart/get/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldReturnCreatedAndHeader_WhenSaveShoppingCartWithUserRole() throws Exception{
        when(shoppingCartService.newShoppingCart(eq(shoppingCartTest)))
                .thenReturn(shoppingCartTest);

        mockMvc.perform(post("/api/v1/cart/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shoppingCartTest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/cart/get/user"));

        verify(shoppingCartService, times(1)).newShoppingCart(eq(shoppingCartTest));
    }

    @Test
    void shouldReturnForbidden_WhenSaveShoppingCartWithNoAuth() throws Exception{
        when(shoppingCartService.newShoppingCart(eq(shoppingCartTest)))
                .thenReturn(shoppingCartTest);

        mockMvc.perform(post("/api/v1/cart/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shoppingCartTest)))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser
    void shouldReturnNoContent_WhenUpdateShoppingCartWithUserRole() throws Exception {
        String shoppingCartId = "123";

        doNothing().when(shoppingCartService).updateShoppingCart(eq(shoppingCartTest), eq(shoppingCartId));

        mockMvc.perform(put("/api/v1/cart/update/{shoppingCartId}", shoppingCartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shoppingCartTest)))
                .andExpect(status().isNoContent());

        verify(shoppingCartService, times(1)).updateShoppingCart(eq(shoppingCartTest), eq(shoppingCartId));
    }

    @Test
    void shouldReturnForbidden_WhenUpdateShoppingCartWithNoAuth() throws Exception {
        String shoppingCartId = "123";

        doNothing().when(shoppingCartService).updateShoppingCart(eq(shoppingCartTest), eq(shoppingCartId));

        mockMvc.perform(put("/api/v1/cart/update/{shoppingCartId}", shoppingCartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shoppingCartTest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldReturnNoContent_WhenDeleteShoppingCartWithUserRole() throws Exception{
        String shoppingCartId = "123";

        doNothing().when(shoppingCartService).deleteShoppingCart(eq(shoppingCartId));

        mockMvc.perform(delete("/api/v1/cart/delete/{shoppingCartId}", shoppingCartId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(shoppingCartService, times(1)).deleteShoppingCart(eq(shoppingCartId));
    }

    @Test
    void shouldReturnForbidden_WhenDeleteShoppingCartWithNoAuth() throws Exception{
        String shoppingCartId = "123";

        doNothing().when(shoppingCartService).deleteShoppingCart(eq(shoppingCartId));

        mockMvc.perform(delete("/api/v1/cart/delete/{shoppingCartId}", shoppingCartId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldNoContent_WhenAddToShoppingCartWithUserRole() throws Exception{
        ProductShoppingCart productShoppingCart = new ProductShoppingCart("123", 2);

        doNothing().when(shoppingCartService).addProductToShoppingCart(eq(productShoppingCart), eq(username));

        mockMvc.perform(patch("/api/v1/cart/add/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productShoppingCart)))
                .andExpect(status().isNoContent());

        verify(shoppingCartService, times(1)).addProductToShoppingCart(eq(productShoppingCart), eq(username));
    }

    @Test
    void shouldReturnForbidden_WhenAddToShoppingCartWithNoAuth() throws Exception{
        ProductShoppingCart productShoppingCart = new ProductShoppingCart("123", 2);

        doNothing().when(shoppingCartService).addProductToShoppingCart(eq(productShoppingCart), eq(username));

        mockMvc.perform(patch("/api/v1/cart/add/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productShoppingCart)))
                .andExpect(status().isForbidden());
    }

    private static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}