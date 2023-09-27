package com.springapi.springapitechnicaltest.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

@Data
public class UserDTO {

    @NonNull
    @NotEmpty
    private String username;

    @NonNull
    @NotEmpty
    private String password;

    private String firstName;

    private String lastName;
}
