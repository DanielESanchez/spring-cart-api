package com.springapi.springapitechnicaltest.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Objects;
import java.util.Set;

@Data
@Document("users")
public class UserModel implements UserDetails {

    @Id
    private String _id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String lastLogin;

    private String createdAt;

    private Set<UserRoleModel> userRoles;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;


    private boolean enabled;

    public UserModel(){
        this.enabled = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked =  true;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Set<UserRoleModel> getAuthorities() {
        return this.userRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserModel user = (UserModel) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
