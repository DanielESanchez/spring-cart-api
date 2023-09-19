package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String categoryId) {
        Category categoryFound = categoryRepository.findCategoryByCategoryId(categoryId).orElseThrow(NotFoundException::new);
        categoryRepository.deleteById(categoryFound.get_id());
    }

    @Override
    public Category updateCategory(Category newCategory) {
        Category categoryFound = categoryRepository.findCategoryByCategoryId(newCategory.getCategoryId()).orElseThrow(NotFoundException::new);
        newCategory.set_id(categoryFound.get_id());
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryByName(String categoryId) {
        return categoryRepository.findCategoryByCategoryId(categoryId).orElseThrow(NotFoundException::new);
    }
}
