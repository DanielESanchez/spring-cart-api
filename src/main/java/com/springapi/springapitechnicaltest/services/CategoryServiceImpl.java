package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Category categoryFound = getCategoryByCategoryId(categoryId);
        categoryRepository.deleteById(categoryFound.get_id());
    }

    @Override
    public Category updateCategory(Category newCategory) {
        Category categoryFound = getCategoryByCategoryId(newCategory.getCategoryId());
        newCategory.set_id(categoryFound.get_id());
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryByCategoryId(String categoryId) {
        return categoryRepository.findCategoryByCategoryId(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id '"+ categoryId + "' could not be found"));
    }

    @Override
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    @Override
    public void disableCategory(String categoryId) {
        Category categoryFound = getCategoryByCategoryId(categoryId);
        categoryFound.setIsEnabled(false);
        categoryRepository.save(categoryFound);
    }

    @Override
    public void enableCategory(String categoryId) {
        Category categoryFound = getCategoryByCategoryId(categoryId);
        categoryFound.setIsEnabled(true);
        categoryRepository.save(categoryFound);
    }
}
