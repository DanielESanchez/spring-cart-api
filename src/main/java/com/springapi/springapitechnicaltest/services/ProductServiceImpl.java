package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = getProductByProductId(productId);
        productRepository.deleteById(product.get_id());
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product newProduct) {
        Product productFound = getProductByProductId(newProduct.getProductId());
        newProduct.set_id(productFound.get_id());
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductByProductId(String productId) {
        return productRepository.findProductByProductId(productId).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findProductsByCategoryId(category);
    }

    @Override
    public List<Product> searchProduct(String text) {
        System.out.println(text);
        return productRepository.findProductsByNameOrDescription(text);
    }

    @Override
    public Product disableProduct(String productId) {
        Product product = getProductByProductId(productId);
        product.setIsEnable(false);
        productRepository.save(product);
        return product;
    }

    @Override
    public Product enableProduct(String productId) {
        Product product = getProductByProductId(productId);
        product.setIsEnable(true);
        productRepository.save(product);
        return product;
    }
}
