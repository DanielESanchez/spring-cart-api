package com.springapi.springapitechnicaltest.models;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("reviews")
public class Review {

    @Id
    private String _id;
    @NonNull
    private String rating;
    @NonNull
    private String username;
    @NonNull
    private String productId;

    private String comment;
}
