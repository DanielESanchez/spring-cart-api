package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product saveProduct(Product product);
    void deleteProduct(String productId);
    List<Product> findAll();
    Product updateProduct(Product newProduct);
    Product getProductByProductId(String productId);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProduct(String name, Optional<String> category);
    Product disableProduct(String productId);
    Product enableProduct(String productId);

}
