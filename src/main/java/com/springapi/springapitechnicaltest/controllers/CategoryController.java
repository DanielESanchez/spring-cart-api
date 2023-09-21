package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/${api.request.path}")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Value("${api.request.path}")
    private String PROPERTY_NAME;

    @PostMapping("/category/new")
    ResponseEntity<HttpHeaders> saveCategory(@RequestBody Category category) {
        Category categorySaved = categoryService.saveCategory(category);
        String location = PROPERTY_NAME + "/category/get/" + categorySaved.getCategoryId();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @GetMapping("/category/get/{categoryId}")
    Category getCategoryByCategoryId(@PathVariable String categoryId) {
        return categoryService.getCategoryByCategoryId(categoryId);
    }

    @PutMapping("/category/update")
    ResponseEntity<HttpStatus> updateCategory(@RequestBody Category category){
        categoryService.updateCategory(category);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/category/delete/{categoryId}")
    ResponseEntity<?> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/category/disable/{categoryId}")
    ResponseEntity<HttpStatus> disableCategory(@PathVariable String categoryId){
        categoryService.disableCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/category/enable/{categoryId}")
    ResponseEntity<HttpStatus> enableCategory(@PathVariable String categoryId){
        categoryService.enableCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
