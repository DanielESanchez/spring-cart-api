package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/${api.request.path}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    @PostMapping("/product/new")
    ResponseEntity saveProduct(@RequestBody Product product) {
        Product productSaved = productService.saveProduct(product);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", PROPERTY_NAME + "/product/get/" + productSaved.getProductId());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @GetMapping("/product/get/{productId}")
    Product getProduct(@PathVariable String productId){
        return productService.getProductByProductId(productId);
    }

    @DeleteMapping("/product/delete/{productId}")
    ResponseEntity<?> deleteCategory(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/product/update")
    ResponseEntity<?> updateCategory(@RequestBody Product product) {
        productService.updateProduct(product);
        return ResponseEntity.ok("Category '" + product.getProductId() + "' updated");
    }

    @GetMapping("/products")
    List<Product> getAllProducts(){
        return productService.findAll();
    }

    @GetMapping("/products/category/{categoryId}")
    List<Product> getProductsByCategoryId(@PathVariable String categoryId){
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/products/search")
    List<Product> searchProduct(@RequestParam(name = "q") String query){
        return productService.searchProduct(query);
    }

    @PatchMapping("/product/disable/{productId}")
    ResponseEntity disableProduct(@PathVariable String productId){
        productService.disableProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/product/enable/{productId}")
    ResponseEntity<?> enableProduct(@PathVariable String productId){
        productService.enableProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
