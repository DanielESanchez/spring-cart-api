package com.springapi.springapitechnicaltest.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

public class UserRoleModel implements GrantedAuthority {

    @Getter
    @Setter
    private RoleModel role;

    @Override
    public String getAuthority() {
        return role.getName();
    }
}