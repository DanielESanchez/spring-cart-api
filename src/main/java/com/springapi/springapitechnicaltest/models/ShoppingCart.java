package com.springapi.springapitechnicaltest.models;

import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document("shopping_carts")
public class ShoppingCart {
    @Id
    private String  _id;

    private String username;

    private Set<ProductShoppingCart> products = new HashSet<>();

    private Float total = (float) 0;

}
