package com.springapi.springapitechnicaltest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("categories")
public class Category {
    @Id
    @Schema(hidden = true)
    private String _id;

    @Indexed(unique = true)
    @NonNull
    private String categoryId;

    @NonNull
    private String name;

    private String description;

    private Boolean isEnabled = true;
}
