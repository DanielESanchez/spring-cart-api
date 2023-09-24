package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.repositories.CategoryRepository;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    private Category category;
    private Product product;
    private Product productToUpdate;
    private List<Product> productList;

    @BeforeEach
    public void setup(){
        category = new Category("cat1","category");
        Set<String> categories = new HashSet<>();
        categories.add(category.getCategoryId());

         product = new Product("prod1","producto1"
                ,"product test",categories,(float) 25.5, 5);
         product.set_id("1");

         productToUpdate = new Product("prod1","producto1"
                ,"product test",categories,(float) 25.5, 5);

         productList = new ArrayList<>();
         productList.add(product);
         productList.add(productToUpdate);

    }


    @Test
    void saveProduct() {
        when(categoryRepository.findCategoryByCategoryId(anyString()))
                .thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(product);

        Product productSaved = productService.saveProduct(product);

        assertNotNull(productSaved);
        assertEquals(productSaved, product);
    }

    @Test
    void deleteProduct() {
        when(productRepository.findProductByProductId(anyString()))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(product.getProductId());

        verify(productRepository, times(1)).deleteById(product.get_id());
    }

    @Test
    void findAll() {
        when(productRepository.findAll())
                .thenReturn(productList);

        productService.findAll();

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProduct() {
        when(categoryRepository.findCategoryByCategoryId(anyString()))
                .thenReturn(Optional.of(category));
        when(productRepository.findProductByProductId(anyString()))
                .thenReturn(Optional.of(product));
        when(productRepository.save(productToUpdate)).thenReturn(product);

        productService.updateProduct(productToUpdate);

        assertEquals(product.get_id(), productToUpdate.get_id());
        verify(productRepository, times(1)).save(productToUpdate);
    }

    @Test
    void getProductByProductId() {
        when(productRepository.findProductByProductId(anyString()))
                .thenReturn(Optional.of(product));

        Product productReceived = productService.getProductByProductId(product.getProductId());

        assertEquals(productReceived, product);
        verify(productRepository, times(1)).findProductByProductId(product.getProductId());

    }

    @Test
    void getProductByProductIdWithNoProductId() {
        when(productRepository.findProductByProductId(product.getProductId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()->productService.getProductByProductId(product.getProductId()));
        verify(productRepository, times(1)).findProductByProductId(product.getProductId());

    }
    @Test
    void getProductByProductIdNotFound() {
        when(productRepository.findProductByProductId(product.getProductId()))
                .thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, ()->productService.getProductByProductId("pro"));
        verify(productRepository, times(1)).findProductByProductId("pro");

    }

    @Test
    void getProductsByCategory() {
        when(categoryRepository.findCategoryByCategoryId(anyString())).thenReturn(Optional.of(category));
        when(productRepository.findProductsByCategoryId(anyString())).thenReturn(productList);

        List<Product> productsReceived = productService.getProductsByCategory("category");
        assertEquals(productList, productsReceived);
        verify(productRepository, times(1)).findProductsByCategoryId("category");
    }

    @Test
    void getProductsByCategoryDisabled() {
        category.setIsEnabled(false);
        when(categoryRepository.findCategoryByCategoryId(anyString())).thenReturn(Optional.of(category));
        when(productRepository.findProductsByCategoryId(anyString())).thenReturn(productList);

        assertThrows(NotFoundException.class, ()-> productService.getProductsByCategory(category.getCategoryId()));
        verify(productRepository, times(0)).findProductsByCategoryId(category.getCategoryId());

        category.setIsEnabled(true);
    }

    @Test
    void disableProduct() {
        when(productRepository.findProductByProductId(anyString())).thenReturn(Optional.of(product));

        productService.disableProduct(product.getProductId());

        assertFalse(product.getIsEnable());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void enableProduct() {
        when(productRepository.findProductByProductId(anyString())).thenReturn(Optional.of(product));

        productService.enableProduct(product.getProductId());

        assertTrue(product.getIsEnable());
        verify(productRepository, times(1)).save(product);
    }
}