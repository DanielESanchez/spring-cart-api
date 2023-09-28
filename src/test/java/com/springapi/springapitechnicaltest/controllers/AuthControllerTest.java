package com.springapi.springapitechnicaltest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.domain.UserDTO;
import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.services.AuthService;
import com.springapi.springapitechnicaltest.models.UserRole;
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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    AuthService authService;
    private UserDTO userDTO;
    private UserDTO adminDTO;
    private JwtAuthenticationResponse jwtResponseUser;
    private JwtAuthenticationResponse jwtResponseAdmin;

    private final UserRole userRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build();
    private final UserRole adminRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_ADMIN).build()).build();


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userDTO = new UserDTO("user", "password");

        adminDTO = new UserDTO("admin", "password");

        Set<UserRole> rolesUser = new HashSet<>();
        rolesUser.add(userRole);

        Set<UserRole> rolesAdmin = new HashSet<>();
        rolesAdmin.add(userRole);
        rolesAdmin.add(adminRole);

        jwtResponseUser = new JwtAuthenticationResponse();
        jwtResponseUser.setToken("token");
        jwtResponseUser.setExpiration(new Date());
        jwtResponseUser.setRoles(rolesUser);

        jwtResponseAdmin = new JwtAuthenticationResponse();
        jwtResponseAdmin.setToken("token");
        jwtResponseAdmin.setExpiration(new Date());
        jwtResponseAdmin.setRoles(rolesAdmin);
    }


    @Test
    void shouldReturnHeader_WhenSavedUser() throws Exception{
        when(authService.signupUser(eq(userDTO), eq("USER")))
                .thenReturn(jwtResponseUser);

        mockMvc.perform(post("/api/v1/user/new")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Token", "token"))
                .andExpect(header().string("Expiration", jwtResponseUser.getExpiration().toString()))
                .andExpect(header().string("Roles", "[ROLE_USER]"));

        verify(authService, times(1)).signupUser(eq(userDTO), eq("USER"));
    }

    @Test
    @WithMockUser(username = "admin", roles={"ADMIN", "USER"})
    void shouldReturnHeader_WhenSavedAdminWithAdminAuth() throws Exception{
        when(authService.signupUser(eq(adminDTO), eq("ADMIN")))
                .thenReturn(jwtResponseAdmin);

        mockMvc.perform(post("/api/v1/user/admin/new")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Token", "token"))
                .andExpect(header().string("Expiration", jwtResponseAdmin.getExpiration().toString()))
                .andExpect(header().exists("Roles"));

        verify(authService, times(1)).signupUser(eq(adminDTO), eq("ADMIN"));
    }

    @Test
    void shouldReturnForbidden_WhenSavedAdminWithNoAuth() throws Exception{
        when(authService.signupUser(eq(adminDTO), eq("ADMIN")))
                .thenReturn(jwtResponseAdmin);

        mockMvc.perform(post("/api/v1/user/admin/new")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminDTO)))
                .andExpect(status().isForbidden());

        verify(authService, times(0)).signupUser(eq(adminDTO), eq("ADMIN"));
    }

    @Test
    @WithMockUser
    void shouldReturnHeader_WhenSavedAdminWithUserRole() throws Exception{
        when(authService.signupUser(eq(adminDTO), eq("ADMIN")))
                .thenReturn(jwtResponseAdmin);

        mockMvc.perform(post("/api/v1/user/admin/new")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminDTO)))
                .andExpect(status().isCreated());

        verify(authService, times(1)).signupUser(eq(adminDTO), eq("ADMIN"));
    }

    @Test
    void shouldReturnHeader_WhenLoginUser() throws Exception{
        LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");

        when(authService.login(eq(loginRequest)))
                .thenReturn(jwtResponseUser);

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Token", "token"))
                .andExpect(header().string("Expiration", jwtResponseUser.getExpiration().toString()))
                .andExpect(header().exists("Roles"));

        verify(authService, times(1)).login(eq(loginRequest));
    }

    @Test
    void shouldReturnHeader_WhenLoginAdmin() throws Exception{
        LoginRequest loginRequest = new LoginRequest("testAdmin", "password");

        when(authService.login(eq(loginRequest)))
                .thenReturn(jwtResponseAdmin);

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Token", "token"))
                .andExpect(header().string("Expiration", jwtResponseAdmin.getExpiration().toString()))
                .andExpect(header().exists("Roles"));

        verify(authService, times(1)).login(eq(loginRequest));
    }

    private static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}