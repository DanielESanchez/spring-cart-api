package com.springapi.springapitechnicaltest.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document("shopping_carts")
public class ShoppingCart {
    @Id
    private String  _id;

    @NonNull
    private String username;

    private Set<ProductShoppingCart> products;

    private Float total;

}
