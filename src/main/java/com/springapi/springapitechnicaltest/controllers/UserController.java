package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.models.UserRole;
import com.springapi.springapitechnicaltest.services.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/${api.request.path}")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping("/user/new")
    ResponseEntity saveUser(@RequestBody User user) {
        JwtAuthenticationResponse response = authService.signupUser(user, "USER");
        HttpHeaders headers = setHeader(response);
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PostMapping("/user/admin/new")
    ResponseEntity saveAdmin(@RequestBody User user) {
        JwtAuthenticationResponse response = authService.signupUser(user, "ADMIN");
        HttpHeaders headers = setHeader(response);
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PostMapping("/user/login")
    ResponseEntity loginUser(@RequestBody LoginRequest loginRequest) {
//        return ResponseEntity.ok(authService.login(loginRequest));
        JwtAuthenticationResponse response = authService.login(loginRequest);
        HttpHeaders headers = setHeader(response);
        return new ResponseEntity(headers, HttpStatus.OK);
    }

    private HttpHeaders setHeader(JwtAuthenticationResponse jwtAuthenticationResponse){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Token", jwtAuthenticationResponse.getToken());
        headers.add("Expiration", jwtAuthenticationResponse.getExpiration().toString());
        String[] array = new String[jwtAuthenticationResponse.getRoles().size()];
        int i = 0;
        for (UserRole role: jwtAuthenticationResponse.getRoles()) {
            array[i++] = role.getAuthority();
        }
        headers.add("Roles", Arrays.toString(array));
        return headers;
    }
}
