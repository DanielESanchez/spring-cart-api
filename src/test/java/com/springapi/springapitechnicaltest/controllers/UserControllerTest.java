package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
import com.springapi.springapitechnicaltest.services.UserService;
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
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    UserService userService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldReturnForbidden_WhenEnableUserWithNoAuth() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/enable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnForbidden_WhenEnableUserWithUserRole() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/enable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void shouldReturnNotFound_WhenEnableUserWithAdminRoleAndNoUsernameProvided() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/enable/user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void shouldReturnNoContent_WhenEnableUserWithAdminRole() throws Exception{
        doNothing().when(userService).enableUser(eq("testUser"));

        mockMvc.perform(patch("/api/v1/admin/enable/user/{username}", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).enableUser(eq("testUser"));
    }

    @Test
    void shouldReturnForbidden_WhenDisableUserWithNoAuth() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/disable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnForbidden_WhenDisableUserWithUserRole() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/disable/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void shouldReturnNotFound_WhenDisableUserWithAdminRoleAndNoUsernameProvided() throws Exception{
        mockMvc.perform(patch("/api/v1/admin/disable/user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void shouldReturnNoContent_WhenDisableUserWithAdminRole() throws Exception{
        doNothing().when(userService).disableUser(eq("testUser"));

        mockMvc.perform(patch("/api/v1/admin/disable/user/{username}", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).disableUser(eq("testUser"));
    }
}