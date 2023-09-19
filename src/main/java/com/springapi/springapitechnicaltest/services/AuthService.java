package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.models.User;

public interface AuthService {
    JwtAuthenticationResponse signupUser(User user, String role);

    JwtAuthenticationResponse login(LoginRequest request);
}
