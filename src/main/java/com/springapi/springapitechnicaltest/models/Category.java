package com.springapi.springapitechnicaltest.models;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("categories")
public class Category {
    @Id
    private String _id;

    @Indexed(unique = true)
    @NonNull
    private String categoryId;

    @NonNull
    private String name;

    private String description;

    private Boolean isEnabled = true;
}
