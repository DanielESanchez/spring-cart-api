package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Order;
import com.springapi.springapitechnicaltest.models.ProductShoppingCart;
import com.springapi.springapitechnicaltest.models.ShoppingCart;
import com.springapi.springapitechnicaltest.services.ShoppingCartService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
public class ShoppingCartController {
    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    private final ShoppingCartService shoppingCartService;

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Shopping cart found for the username received", content = { @Content(schema = @Schema(implementation = ShoppingCart.class)) }),
            @ApiResponse(responseCode = "404", description = "User or shopping cart not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/cart/get/{username}")
    public ShoppingCart getShoppingCart(@PathVariable String username ){
        return shoppingCartService.findShoppingCartByUsername(username);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Shopping cart saved successfully", headers = {
                    @Header(name = HttpHeaders.LOCATION, schema =
                    @Schema(type = "string"),
                            description = "Path of the cart saved") },
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema) }),
    })
    @PostMapping("/cart/new")
    public ResponseEntity<?> saveShoppingCart(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart shoppingCartSaved = shoppingCartService.newShoppingCart(shoppingCart);
        String location = PROPERTY_NAME + "/cart/get/" + shoppingCartSaved.getUsername();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Shopping cart updated", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User or shopping cart not found", content = { @Content(schema = @Schema) })
    })
    @PutMapping("/cart/update/{shoppingCartId}")
    public ResponseEntity<?> updateShoppingCart(@RequestBody ShoppingCart shoppingCart, @PathVariable String shoppingCartId ){
        shoppingCartService.updateShoppingCart(shoppingCart, shoppingCartId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Shopping cart deleted", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User or shopping cart not found", content = { @Content(schema = @Schema) })
    })
    @DeleteMapping("/cart/delete/{shoppingCartId}")
    public ResponseEntity<?> deleteShoppingCart(@PathVariable String shoppingCartId){
        shoppingCartService.deleteShoppingCart(shoppingCartId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Product added to the shopping cart for the username received", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User or shopping cart not found", content = { @Content(schema = @Schema) })
    })
    @PatchMapping("/cart/add/{username}")
    public ResponseEntity<?> addToShoppingCart(@PathVariable String username, @RequestBody ProductShoppingCart productShoppingCart){
        shoppingCartService.addProductToShoppingCart(productShoppingCart, username);
        return ResponseEntity.noContent().build();
    }
}
