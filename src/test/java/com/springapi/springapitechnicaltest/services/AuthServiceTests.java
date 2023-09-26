package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.domain.UserDTO;
import com.springapi.springapitechnicaltest.models.Role;
import com.springapi.springapitechnicaltest.models.RoleName;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.models.UserRole;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AuthServiceTests {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthServiceImpl authService;

    private User user;
    private User admin;

    private UserDTO userDTO;
    private UserDTO adminDTO;

    @BeforeEach
    public void setup(){
        UserRole adminRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_ADMIN).build()).build();
        UserRole userRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build();

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole);

        Set<UserRole> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);

        user = new User();
        user.setUserRoles(userRoles);
        user.setUsername("user");
        user.setUsername("password");

        admin = new User();
        admin.setUserRoles(adminRoles);
        admin.setUsername("admin");
        admin.setUsername("password");

        userDTO = new UserDTO("user", "password");
        adminDTO = new UserDTO("admin", "password");

    }
    @Test
    void testSaveUser(){

        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.extractExpiration(anyString())).thenReturn(new Date());
        when(userRepository.save(any(User.class))).thenReturn(user);

        JwtAuthenticationResponse response = authService.signupUser(userDTO, "USER");

        assertNotNull(response);
        assertEquals(response.getToken(), "jwt-token");
        assertNotNull(response.getExpiration());
        assertEquals(response.getRoles().size(), user.getUserRoles().size());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSaveAdmin(){
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.extractExpiration(anyString())).thenReturn(new Date());
        when(userRepository.save(any(User.class))).thenReturn(admin);

        JwtAuthenticationResponse response = authService.signupUser(adminDTO, "ADMIN");

        assertNotNull(response);
        assertEquals(response.getToken(), "jwt-token");
        assertNotNull(response.getExpiration());
        assertEquals(response.getRoles().size(), admin.getUserRoles().size());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogin(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(user.getPassword());

        when(userService.findUserByUsername(eq(user.getUsername())))
                .thenReturn(user);

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("token123");
        when(jwtService.extractExpiration(anyString()))
                .thenReturn(new Date());

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        JwtAuthenticationResponse response = authService.login(loginRequest);

        assertEquals("token123", response.getToken());
        assertNotNull(response.getExpiration());
        assertEquals(response.getRoles(), user.getUserRoles());

        verify(userService, times(1)).findUserByUsername(eq(user.getUsername()));

        SecurityContextHolder.clearContext();
    }
}
