package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.domain.JwtAuthenticationResponse;
import com.springapi.springapitechnicaltest.domain.LoginRequest;
import com.springapi.springapitechnicaltest.domain.UserDTO;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.models.UserRole;
import com.springapi.springapitechnicaltest.services.AuthService;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User saved",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "500", description = "Username already exists",
                    content = @Content)})
    @PostMapping("/user/new")
    void saveUser(@RequestBody UserDTO user, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.signupUser(user, "USER");
        response.setStatus(201);
        response.addHeader("Token", jwtResponse.getToken());
        response.addHeader("Expires", jwtResponse.getExpiration().toString());
        response.addHeader("Roles", getRolesUser(jwtResponse.getRoles()));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User saved",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "403", description = "You are not logged in to do this",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Username already exists",
                    content = @Content)})
    @PostMapping("/user/admin/new")
    void saveAdmin(@RequestBody UserDTO user, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.signupUser(user, "ADMIN");
        response.setStatus(201);
        response.addHeader("Token", jwtResponse.getToken());
        response.addHeader("Expires", jwtResponse.getExpiration().toString());
        response.addHeader("Roles", getRolesUser(jwtResponse.getRoles()));
    }

    @PostMapping("/user/login")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "User logged in successfully", headers = {
                    @Header(name = "Token", schema =
                    @Schema(type = "string"),
                            description = "Jwt token to used in authentication secured endpoints"),
                    @Header(name = "Expires", schema =
                    @Schema(type = "string"),
                            description = "Expiration date for the jwt token"),
                    @Header(name = "Roles", schema =
                    @Schema(type = "string"),
                            description = "Roles array for the user logged in")
            }),
            @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Username or password incorrect",
                    content = { @Content(schema = @Schema) })
    })
    void loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.login(loginRequest);
        response.setStatus(200);
        response.addHeader("Token", jwtResponse.getToken());
        response.addHeader("Expires", jwtResponse.getExpiration().toString());
        response.addHeader("Roles", getRolesUser(jwtResponse.getRoles()));
    }

    private String getRolesUser(Set<UserRole> roleSet){
        String[] arrayRoles = new String[roleSet.size()];
        int indexRole = 0;
        for (UserRole role: roleSet) {
            arrayRoles[indexRole++] = role.getAuthority();
        }
        return Arrays.toString(arrayRoles);
    }
}
