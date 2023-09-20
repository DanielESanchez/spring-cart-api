package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.ProductShoppingCart;
import com.springapi.springapitechnicaltest.models.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart newShoppingCart(ShoppingCart shoppingCart);
    void deleteShoppingCart(String shoppingCartId);
    ShoppingCart updateShoppingCart(ShoppingCart shoppingCart, String shoppingCartId);
    ShoppingCart addProductToShoppingCart(ProductShoppingCart productShoppingCart, String username);

    ShoppingCart findShoppingCartByUsername(String username);

}
