package com.springapi.springapitechnicaltest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.models.Order;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
import com.springapi.springapitechnicaltest.services.OrderService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    OrderService orderService;
    String orderId = "1";
    String username = "user";
    String authorizationHeader = "Bearer token123";
    Order order1 = new Order();
    Order order2 = new Order();
    List<Order> sampleOrders;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        order1.set_id("1");
        order1.setUsername(username);
        order1.setTotal( (float) 100.0);
        order2.set_id("2");
        order2.setUsername(username);
        order2.setTotal( (float) 100.0);

        sampleOrders = Arrays.asList(
                order1,
                order2
        );
    }


    @Test
    @WithMockUser
    void saveOrder() throws Exception{
        Order sampleOrder = new Order();
        sampleOrder.set_id("123");
        String authorizationHeader = "Bearer token123";

        when(orderService.saveOrder(eq(sampleOrder), eq(authorizationHeader)))
                .thenReturn(sampleOrder);

        mockMvc.perform(post("/api/v1/order/new")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleOrder)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/order/get/123"));

        verify(orderService, times(1)).saveOrder(eq(sampleOrder), eq(authorizationHeader));
    }

    @Test
    void saveOrderNoAuth() throws Exception{

        when(orderService.saveOrder(eq(order1), eq(authorizationHeader)))
                .thenReturn(order1);

        mockMvc.perform(post("/api/v1/order/new")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(order1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void buyOrder() throws Exception {
        String orderId = "1";

        doNothing().when(orderService).buyOrder(eq(orderId), eq(authorizationHeader));

        mockMvc.perform(patch("/api/v1/order/buy/{orderId}", orderId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).buyOrder(eq(orderId), eq(authorizationHeader));

    }

    @Test
    void buyOrderNoAuth() throws Exception {
        String orderId = "1";

        doNothing().when(orderService).buyOrder(eq(orderId), eq(authorizationHeader));

        mockMvc.perform(patch("/api/v1/order/buy/{orderId}", orderId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void completeOrder() throws Exception{
        doNothing().when(orderService).completeOrder(eq(orderId));

        mockMvc.perform(patch("/api/v1/order/complete/{orderId}", orderId))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).completeOrder(eq(orderId));

    }

    @Test
    @WithMockUser
    void completeOrderRoleUser() throws Exception{
        doNothing().when(orderService).completeOrder(eq(orderId));

        mockMvc.perform(patch("/api/v1/order/complete/{orderId}", orderId))
                .andExpect(status().isForbidden());

    }

    @Test
    void completeOrderNoAuth() throws Exception{
        doNothing().when(orderService).completeOrder(eq(orderId));

        mockMvc.perform(patch("/api/v1/order/complete/{orderId}", orderId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser
    void cancelOrderByUser() throws Exception{
        doNothing().when(orderService).cancelOrder(eq(orderId), anyString(), eq(authorizationHeader));

        mockMvc.perform(patch("/api/v1/order/cancel/{orderId}", orderId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).cancelOrder(eq(orderId), eq("Canceled by user"), eq(authorizationHeader));
    }

    @Test
    void cancelOrderNoAuth() throws Exception{
        doNothing().when(orderService).cancelOrder(eq(orderId), anyString(), eq(authorizationHeader));

        mockMvc.perform(patch("/api/v1/order/cancel/{orderId}", orderId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void refundOrder() throws Exception {
        doNothing().when(orderService).refundOrder(eq("1"));

        mockMvc.perform(patch("/api/v1/order/refund/{orderId}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).refundOrder(eq("1"));
    }

    @Test
    @WithMockUser
    void refundOrderRoleUser() throws Exception {
        doNothing().when(orderService).refundOrder(eq("1"));

        mockMvc.perform(patch("/api/v1/order/refund/{orderId}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void refundOrderNoAuth() throws Exception {
        doNothing().when(orderService).refundOrder(eq("1"));

        mockMvc.perform(patch("/api/v1/order/refund/{orderId}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser
    void getOrderById() throws Exception{
        when(orderService.findOrderById(eq(orderId), anyString()))
                .thenReturn(order1);

        mockMvc.perform(get("/api/v1/order/get/{orderId}", orderId)
                        .header("Authorization", "Bearer your-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._id", is(orderId)))
                .andExpect(jsonPath("$.total", is(100.0)));

        verify(orderService, times(1)).findOrderById(eq(orderId), anyString());
    }

    @Test
    void getOrderByIdNoUser() throws Exception{
        when(orderService.findOrderById(eq(orderId), anyString()))
                .thenReturn(order1);

        mockMvc.perform(get("/api/v1/order/get/{orderId}", orderId)
                        .header("Authorization", "Bearer your-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getOrdersByUsername() throws Exception {
        when(orderService.findOrdersByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findOrdersByUsername(eq(username), eq(authorizationHeader));
    }

    @Test
    void getOrdersByUsernameNoAuth() throws Exception {
        when(orderService.findOrdersByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser
    void getOrdersCompletedByUsername() throws Exception{
        when(orderService.findOrdersCompletedByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/completed/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findOrdersCompletedByUsername(eq(username), eq(authorizationHeader));
    }

    @Test
    void getOrdersCompletedByUsernameNoAuth() throws Exception{
        when(orderService.findOrdersCompletedByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/completed/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getOrdersCanceledByUsername() throws Exception {
        when(orderService.findOrdersCanceledByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/canceled/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findOrdersCanceledByUsername(eq(username), eq(authorizationHeader));
    }

    @Test
    void getOrdersCanceledByUsernameNoAuth() throws Exception {
        when(orderService.findOrdersCanceledByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/canceled/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getOrdersRefundedByUsername() throws Exception {
        when(orderService.findOrdersRefundedByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/refunded/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findOrdersRefundedByUsername(eq(username), eq(authorizationHeader));
    }

    @Test
    void getOrdersRefundedByUsernameNoAuth() throws Exception {
        when(orderService.findOrdersRefundedByUsername(eq(username), eq(authorizationHeader)))
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/get/refunded/{username}", username)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllOrders() throws Exception{
        when(orderService.findAllOrders())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findAllOrders();
    }

    @Test
    void getAllOrdersNoAuth() throws Exception{
        when(orderService.findAllOrders())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getAllOrdersRoleUser() throws Exception{
        when(orderService.findAllOrders())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllOrdersCompleted() throws Exception {
        when(orderService.findAllOrdersCompleted())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findAllOrdersCompleted();
    }

    @Test
    @WithMockUser
    void getAllOrdersCompletedRoleUser() throws Exception {
        when(orderService.findAllOrdersCompleted())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/completed"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrdersCompletedNoAuth() throws Exception {
        when(orderService.findAllOrdersCompleted())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/completed"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllOrdersCanceled() throws Exception {
        when(orderService.findAllOrdersCanceled())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/canceled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findAllOrdersCanceled();
    }

    @Test
    @WithMockUser
    void getAllOrdersCanceledUserRole() throws Exception {
        when(orderService.findAllOrdersCanceled())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/canceled"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrdersCanceledNoAuth() throws Exception {
        when(orderService.findAllOrdersCanceled())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/canceled"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllOrdersRefunded() throws Exception {
        when(orderService.findAllOrdersRefunded())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/refunded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]._id", is("1")))
                .andExpect(jsonPath("$[0].username", is("user")))
                .andExpect(jsonPath("$[1]._id", is("2")))
                .andExpect(jsonPath("$[1].username", is("user")));

        verify(orderService, times(1)).findAllOrdersRefunded();
    }

    @Test
    @WithMockUser
    void getAllOrdersRefundedRoleUser() throws Exception {
        when(orderService.findAllOrdersRefunded())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/refunded"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrdersRefundedNoAuth() throws Exception {
        when(orderService.findAllOrdersRefunded())
                .thenReturn(sampleOrders);

        mockMvc.perform(get("/api/v1/orders/all/get/refunded"))
                .andExpect(status().isForbidden());
    }

    private static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}