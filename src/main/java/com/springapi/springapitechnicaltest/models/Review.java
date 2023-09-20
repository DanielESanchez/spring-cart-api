package com.springapi.springapitechnicaltest.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("reviews")
@CompoundIndexes({
        @CompoundIndex(name = "unique_username_productId", def = "{'username': 1, 'productId': 1}", unique = true)
})
public class Review {

    @Id
    private String _id;

    @NonNull
    @Min(value = 1, message = "Minimum value is 1")
    @Max(value = 5, message = "Maximum value is 5")
    private Integer rating;
    @NonNull
    private String username;
    @NonNull
    private String productId;

    private String comment;
}
