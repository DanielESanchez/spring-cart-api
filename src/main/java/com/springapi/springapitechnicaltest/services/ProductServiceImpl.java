package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product newProduct) {
        for ( String categoryId: newProduct.getCategoriesId() ) {
            categoryService.getCategoryByCategoryId(categoryId);
        }
        Product productFound = getProductByProductId(newProduct.getProductId());
        newProduct.set_id(productFound.get_id());
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductByProductId(String productId) {
        Product productFound = productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with id '"+ productId + "' could not be found"));
        if(!productFound.getIsEnable()) throw  new NotFoundException("Product with id '"+ productId + "' could not be found");
        return productFound;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        categoryService.getCategoryByCategoryId(category);
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
