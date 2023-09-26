package com.springapi.springapitechnicaltest.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserDTO {

    @NonNull
    private String username;

    @NonNull
    private String password;

    private String firstName;

    private String lastName;
}
