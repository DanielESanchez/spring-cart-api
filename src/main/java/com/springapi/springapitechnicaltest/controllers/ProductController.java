package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.services.ProductService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Value("${api.request.path}")
    private String PROPERTY_NAME;

    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Product saved successfully", headers = {
                    @Header(name = HttpHeaders.LOCATION, schema =
                    @Schema(type = "string"),
                            description = "Path of the product saved") },
                    content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/product/new")
    ResponseEntity<HttpHeaders> saveProduct(@RequestBody Product product) {
        Product productSaved = productService.saveProduct(product);
        String location = PROPERTY_NAME + "/product/get/" + productSaved.getProductId();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing the product saved with the id received",
                    content = { @Content(schema = @Schema(implementation = Product.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/product/get/{productId}")
    Product getProduct(@PathVariable String productId){
        return productService.getProductByProductId(productId);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Product deleted",
                    content = { @Content(schema = @Schema) }),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/product/delete/{productId}")
    ResponseEntity<HttpStatus> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Product updated",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/product/update")
    ResponseEntity<HttpStatus> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing list of all products saved"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/products")
    List<Product> getAllProducts(){
        return productService.findAll();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing list of all products saved for the category received")
    })
    @GetMapping("/products/category/{categoryId}")
    List<Product> getProductsByCategoryId(@PathVariable String categoryId){
        return productService.getProductsByCategory(categoryId);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Showing list af all products saved for the search received"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/products/search")
    List<Product> searchProduct(@RequestParam(name = "q") String query, @RequestParam Optional<String> category){
        return productService.searchProduct(query, category);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Product marked as disabled",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/product/disable/{productId}")
    ResponseEntity<HttpStatus> disableProduct(@PathVariable String productId){
        productService.disableProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Product marked as enabled",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/product/enable/{productId}")
    ResponseEntity<HttpStatus> enableProduct(@PathVariable String productId){
        productService.enableProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
