package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.models.UserRole;
import com.springapi.springapitechnicaltest.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/user/new")
    void saveUser(@RequestBody User user, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.signupUser(user, "USER");
        response.setStatus(201);
        response.addHeader("Token", jwtResponse.getToken());
        response.addHeader("Expires", jwtResponse.getExpiration().toString());
        response.addHeader("Roles", getRolesUser(jwtResponse.getRoles()));
    }

    @PostMapping("/user/admin/new")
    void saveAdmin(@RequestBody User user, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.signupUser(user, "ADMIN");
        response.setStatus(201);
        response.addHeader("Token", jwtResponse.getToken());
        response.addHeader("Expires", jwtResponse.getExpiration().toString());
        response.addHeader("Roles", getRolesUser(jwtResponse.getRoles()));
    }

    @PostMapping("/user/login")
    void loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.login(loginRequest);
        response.setStatus(200);
        response.addHeader("Token", jwtResponse.getToken());
        response.addHeader("Expires", jwtResponse.getExpiration().toString());
        response.addHeader("Roles", getRolesUser(jwtResponse.getRoles()));
    }

    private String getRolesUser(Set<UserRole> roleSet){
        String[] array = new String[roleSet.size()];
        int i = 0;
        for (UserRole role: roleSet) {
            array[i++] = role.getAuthority();
        }
        return Arrays.toString(array);
    }
}
