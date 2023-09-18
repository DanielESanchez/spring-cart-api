package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.dao.LoginRequest;
import com.springapi.springapitechnicaltest.models.UserModel;
import com.springapi.springapitechnicaltest.models.UserRoleModel;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springapi.springapitechnicaltest.domain.dao.JwtAuthenticationResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public JwtAuthenticationResponse signupUser(UserModel user) {
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(jwt);
        Set<UserRoleModel> roles = user.getUserRoles();
        return JwtAuthenticationResponse.builder().token(jwt).expiration(expiration).roles(roles).build();
    }

    @Override
    public JwtAuthenticationResponse signupAdmin(UserModel user) {
        return null;
    }


    @Override
    public JwtAuthenticationResponse login(LoginRequest request) throws UsernameNotFoundException {
        UserModel user = userRepository.findUserByUsername(request.getUsername());
        if(user== null){
            throw new UsernameNotFoundException("User or Password Incorrect.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String jwt = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(jwt);
        Set<UserRoleModel> roles = user.getUserRoles();
        return JwtAuthenticationResponse.builder().token(jwt).expiration(expiration).roles(roles).build();
    }
}