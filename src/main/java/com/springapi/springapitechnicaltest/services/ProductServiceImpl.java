package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    @Override
    public Product saveProduct(Product product) {
        for ( String categoryId: product.getCategoriesId() ) {
            categoryService.getCategoryByCategoryId(categoryId);
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = getProductByProductId(productId);
        productRepository.deleteById(product.get_id());
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAllUser();
    }

    @Override
    public List<Product> findAllAdmin() {
        return productRepository.findAll();
    }

    @Override
    public void updateProduct(Product newProduct) {
        for ( String categoryId: newProduct.getCategoriesId() ) {
            categoryService.getCategoryByCategoryId(categoryId);
        }
        Product productFound = getProductByProductId(newProduct.getProductId());
        newProduct.set_id(productFound.get_id());
        log.info(new Date() + " The product " + productFound.getProductId() + " has been updated ");
        productRepository.save(newProduct);
    }

    @Override
    public Product getProductByProductId(String productId) {
        Product productFound = productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with id '"+ productId + "' could not be found"));
        return productFound;
    }

    @Override
    public Product getProductByProductIdAdmin(String productId) {
        Product productFound = productRepository.findProductByProductIdAdmin(productId)
                .orElseThrow(() -> new NotFoundException("Product with id '"+ productId + "' could not be found"));
        return productFound;
    }

    private Product getProductByProductIdToDisableEnable(String productId) {
        Product productFound = productRepository.findProductToEnableDisable(productId)
                .orElseThrow(() -> new NotFoundException("Product with id '"+ productId + "' could not be found"));
        return productFound;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        Category categoryFound = categoryService.getCategoryByCategoryId(category);
        if(!categoryFound.getIsEnabled()) throw new NotFoundException("Category with id '"+ category + "' could not be found");
        return productRepository.findProductsByCategoryId(category);
    }

    @Override
    public List<Product> searchProduct(String text, Optional<String> category) {
        if(category.isPresent() && !category.get().isEmpty()) {
           Category categoryFound = categoryService.getCategoryByCategoryId(category.get());
            if(!categoryFound.getIsEnabled()) throw new NotFoundException("Category with id '"+ category.get() + "' could not be found");
        }
        return productRepository.findProductsByNameOrDescription(text, category);
    }

    @Override
    public void disableProduct(String productId) {
        Product product = getProductByProductIdToDisableEnable(productId);
        product.setIsEnable(false);
        productRepository.save(product);
        log.info(new Date() + " The product " + product + " has been disabled " );
    }

    @Override
    public void enableProduct(String productId) {
        Product product = getProductByProductIdToDisableEnable(productId);
        product.setIsEnable(true);
        productRepository.save(product);
        log.info(new Date() + " The product " + product + " has been enabled " );
    }

}
