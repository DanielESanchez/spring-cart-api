package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.domain.UserDTO;
import com.springapi.springapitechnicaltest.models.User;

public interface AuthService {
    JwtAuthenticationResponse signupUser(UserDTO userDTO, String role);

    JwtAuthenticationResponse login(LoginRequest request);
}
