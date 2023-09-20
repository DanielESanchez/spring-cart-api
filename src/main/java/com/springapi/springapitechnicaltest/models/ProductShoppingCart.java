package com.springapi.springapitechnicaltest.models;

import lombok.Data;

@Data
public class ProductShoppingCart {
    private String productId;
    private Integer quantity;
    private Float price;
    private Float total;
}
