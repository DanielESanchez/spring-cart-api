package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.models.Role;
import com.springapi.springapitechnicaltest.models.RoleName;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.models.UserRole;
import com.springapi.springapitechnicaltest.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    @Override
    public JwtAuthenticationResponse signupUser(User user, String role) {
        Set<UserRole> roles = new HashSet<>();
        if(role.equals("ADMIN")){
            roles.add(UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build());
            roles.add(UserRole.builder().role(Role.builder().name(RoleName.ROLE_ADMIN).build()).build());
        }else {
            roles.add(UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build());
        }
        user.setUserRoles(roles);
        user.setCreatedAt(new Date().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(jwt);
        return JwtAuthenticationResponse.builder().token(jwt).expiration(expiration).roles(roles).build();
    }


    @Override
    public JwtAuthenticationResponse login(LoginRequest request) throws UsernameNotFoundException {
        User user = userService.findUserByUsername(request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String jwt = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(jwt);
        Set<UserRole> roles = user.getUserRoles();
        return JwtAuthenticationResponse.builder().token(jwt).expiration(expiration).roles(roles).build();
    }
}