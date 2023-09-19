package com.springapi.springapitechnicaltest.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Builder
public class UserRole implements GrantedAuthority {

    @Getter
    @Setter
    private Role role;

    @Override
    public String getAuthority() {
        return role.getName().toString();
    }
}