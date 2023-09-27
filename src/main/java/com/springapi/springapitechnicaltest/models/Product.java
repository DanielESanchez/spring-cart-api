package com.springapi.springapitechnicaltest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document("products")
public class Product {
    @Id
    @Schema(hidden = true)
    private String _id;

    @NonNull
    @Indexed(unique = true)
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
    private Float price;

    @NonNull
    private Integer quantityAvailable;

    private Boolean isEnable = true;
}
