package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.dao.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.dao.LoginRequest;
import com.springapi.springapitechnicaltest.models.UserModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService {
    JwtAuthenticationResponse signupUser(UserModel user);
    JwtAuthenticationResponse signupAdmin(UserModel user);

    JwtAuthenticationResponse login(LoginRequest request);
}
