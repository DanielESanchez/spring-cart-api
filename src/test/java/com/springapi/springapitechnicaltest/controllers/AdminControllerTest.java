package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.services.AdminService;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class AdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    AdminService adminService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void enableUserWithNoAuth() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/enable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void enableUserWithUserRole() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/enable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void enableUserWithAdminNoUsernameProvided() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/enable/user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void enableUser() throws Exception{
        doNothing().when(adminService).enableUser(eq("testUser"));

        mockMvc.perform(patch("/api/v1/admin/enable/user/{username}", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).enableUser(eq("testUser"));
    }

    @Test
    void disableUserWithNoAuth() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/disable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void disableUserWithUserRole() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/disable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void disableUserWithAdminNoUsernameProvided() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/disable/user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void disableUser() throws Exception{
        doNothing().when(adminService).disableUser(eq("testUser"));

        mockMvc.perform(patch("/api/v1/admin/disable/user/{username}", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).disableUser(eq("testUser"));
    }
}