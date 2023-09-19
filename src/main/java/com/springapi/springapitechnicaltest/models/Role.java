package com.springapi.springapitechnicaltest.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document("roles")
public class Role {

    @Getter
    @Setter
    private RoleName name;
}
