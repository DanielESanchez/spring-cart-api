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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() throws UsernameNotFoundException {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findUserByUsername(username);
            }
        };
    }
}
