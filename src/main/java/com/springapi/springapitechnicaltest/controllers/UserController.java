package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.domain.dao.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.dao.LoginRequest;
import com.springapi.springapitechnicaltest.models.RoleModel;
import com.springapi.springapitechnicaltest.models.UserModel;
import com.springapi.springapitechnicaltest.models.UserRoleModel;
import com.springapi.springapitechnicaltest.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/user/new")
    ResponseEntity saveUser(@RequestBody UserModel user) {
        Set<UserRoleModel> roles = new HashSet<>();
        roles.add(UserRoleModel.builder().role(RoleModel.builder().name("ROLE_ADMIN").build()).build());
        user.setUserRoles(roles);
        user.setCreatedAt(new Date().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        JwtAuthenticationResponse response = authService.signupUser(user);
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
        for (UserRoleModel role: jwtAuthenticationResponse.getRoles()) {
            array[i++] = role.getAuthority();
        }
        headers.add("Roles", Arrays.toString(array));
        return headers;
    }
}
