package com.springapi.springapitechnicaltest.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements GrantedAuthority {

    @Getter
    @Setter
    private Role role;

    @Override
    public String getAuthority() {
        return role.getName().toString();
    }
}