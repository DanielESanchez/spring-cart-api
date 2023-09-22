package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.ProductShoppingCart;
import com.springapi.springapitechnicaltest.models.ShoppingCart;
import com.springapi.springapitechnicaltest.services.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
public class ShoppingCartController {
    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    private final ShoppingCartService shoppingCartService;

    @GetMapping("/cart/get/{username}")
    public ShoppingCart getShoppingCart(@PathVariable String username ){
        return shoppingCartService.findShoppingCartByUsername(username);
    }

    @PostMapping("/cart/new")
    public ResponseEntity<?> saveShoppingCart(@RequestBody ShoppingCart shoppingCart){
        System.out.println(shoppingCart);
        ShoppingCart shoppingCartSaved = shoppingCartService.newShoppingCart(shoppingCart);
        String location = PROPERTY_NAME + "/cart/get/" + shoppingCartSaved.getUsername();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @PutMapping("/cart/update/{shoppingCartId}")
    public ResponseEntity<?> updateShoppingCart(@RequestBody ShoppingCart shoppingCart, @PathVariable String shoppingCartId ){
        shoppingCartService.updateShoppingCart(shoppingCart, shoppingCartId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart/delete/{shoppingCartId}")
    public ResponseEntity<?> deleteShoppingCart(@PathVariable String shoppingCartId){
        shoppingCartService.deleteShoppingCart(shoppingCartId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cart/add/{username}")
    public ResponseEntity<?> addToShoppingCart(@PathVariable String username, @RequestBody ProductShoppingCart productShoppingCart){
        shoppingCartService.addProductToShoppingCart(productShoppingCart, username);
        return ResponseEntity.noContent().build();
    }
}
