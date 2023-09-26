package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Category;

import java.util.List;

public interface CategoryService {
    Category saveCategory(Category category);
    void deleteCategory(String categoryId);
    Category updateCategory(Category newCategory);
    Category getCategoryByCategoryId(String categoryId);
    List<Category> getAllCategories();
    void disableCategory(String categoryId);
    void enableCategory(String categoryId);
}
