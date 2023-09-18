package com.springapi.springapitechnicaltest.domain.dao;


import com.springapi.springapitechnicaltest.models.UserRoleModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private Date expiration;
    private Set<UserRoleModel> roles;
}