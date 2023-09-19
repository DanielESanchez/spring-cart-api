package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Category;

public interface CategoryService {
    Category saveCategory(Category category);
    void deleteCategory(String categoryId);
    Category updateCategory(Category newCategory);
    Category getCategoryByName(String categoryId);
}
