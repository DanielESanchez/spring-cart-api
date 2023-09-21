package com.springapi.springapitechnicaltest.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class ProductShoppingCart {
    @NonNull
    private String productId;
    @NonNull
    private Integer quantity;

    private Float price;

    private Float total;
}
