package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product saveProduct(Product product);
    void deleteProduct(String productId);
    List<Product> findAll();
    void updateProduct(Product newProduct);
    Product getProductByProductId(String productId);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProduct(String name, Optional<String> category);
    void disableProduct(String productId);
    void enableProduct(String productId);

}
