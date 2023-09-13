package com.springapi.springapitechnicaltest.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("roles")
public class RoleModel {

    @Getter
    @Setter
    private String name;
}
