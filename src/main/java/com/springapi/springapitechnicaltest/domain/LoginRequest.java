package com.springapi.springapitechnicaltest.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
public class LoginRequest {

    @NonNull
    @NotEmpty
    private String username;
    @NonNull
    @NotEmpty
    private String password;
}