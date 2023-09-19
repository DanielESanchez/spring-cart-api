package com.springapi.springapitechnicaltest.models;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document("products")
public class Product {
    @Id
    private String _id;

    @NonNull
    private String productId;

    @NonNull
    @TextIndexed
    private String name;

    @NonNull
    @TextIndexed
    private String description;

    @NonNull
    private Set<String> categoriesId;

    @NonNull
    private Boolean isEnable = true;
}
