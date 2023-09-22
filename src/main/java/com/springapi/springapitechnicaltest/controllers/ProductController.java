package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    @PostMapping("/product/new")
    ResponseEntity<HttpHeaders> saveProduct(@RequestBody Product product) {
        Product productSaved = productService.saveProduct(product);
        String location = PROPERTY_NAME + "/product/get/" + productSaved.getProductId();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @GetMapping("/product/get/{productId}")
    Product getProduct(@PathVariable String productId){
        return productService.getProductByProductId(productId);
    }

    @DeleteMapping("/product/delete/{productId}")
    ResponseEntity<HttpStatus> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/product/update")
    ResponseEntity<HttpStatus> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
    List<Product> getAllProducts(){
        return productService.findAll();
    }

    @GetMapping("/products/category/{categoryId}")
    List<Product> getProductsByProductId(@PathVariable String categoryId){
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/products/search")
    List<Product> searchProduct(@RequestParam(name = "q") String query, @RequestParam Optional<String> category){
        return productService.searchProduct(query, category);
    }

    @PatchMapping("/product/disable/{productId}")
    ResponseEntity<HttpStatus> disableProduct(@PathVariable String productId){
        productService.disableProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/product/enable/{productId}")
    ResponseEntity<HttpStatus> enableProduct(@PathVariable String productId){
        productService.enableProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
