package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;
    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    void shouldReturnCategorySaved_WhenSaveCategory() {
        Category categoryToSave = new Category("cat1","category");
        categoryToSave.set_id("1");

        Category savedCategory = new Category("cat1","category 2");
        savedCategory.set_id("2");
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.saveCategory(categoryToSave);

        verify(categoryRepository, times(1)).save(eq(categoryToSave));

        assertEquals("2", result.get_id());
        assertEquals("category 2", result.getName());
    }

    @Test
    void shouldReturnNothing_WhenDeleteCategory() {
        String categoryId = "1";
        Category category = new Category("cat1","category");
        category.set_id(categoryId);

        when(categoryRepository.findCategoryByCategoryId(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void shouldReturnCategoryUpdated_WhenUpdateCategory() {
        Category categoryUpdated = new Category("cat1","category");
        categoryUpdated.set_id("1");

        Category savedCategory = new Category("cat1","category 2");
        savedCategory.set_id("2");

        when(categoryRepository.findCategoryByCategoryId(savedCategory.getCategoryId())).thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryUpdated);

        Category result = categoryService.updateCategory(categoryUpdated);

        verify(categoryRepository, times(1)).save(eq(categoryUpdated));

        assertEquals("2", result.get_id());
        assertEquals("category", result.getName());
    }

    @Test
    void shouldReturnCategory_WhenGetCategoryByCategoryId() {
        String _id = "1";
        Category expectedCategory = new Category("cat1", "category");
        expectedCategory.set_id(_id);
        when(categoryRepository.findCategoryByCategoryId(_id)).thenReturn(Optional.of(expectedCategory));

        Category resultCategory = categoryService.getCategoryByCategoryId(_id);

        assertEquals(expectedCategory, resultCategory);
    }

    @Test
    void shouldReturnNotFound_WhenGetCategoryByCategoryIdNotSaved() {
        String categoryId = "123";
        when(categoryRepository.findCategoryByCategoryId(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryByCategoryId(categoryId));
    }

    @Test
    void shouldReturnNothing_WhenDisableCategory() {
        String _id = "123";
        Category category = new Category("cat1", "category");
        category.set_id(_id);
        category.setIsEnabled(true);

        when(categoryRepository.findCategoryByCategoryId(anyString())).thenReturn(Optional.of(category));

        categoryService.disableCategory(_id);

        verify(categoryRepository, times(1)).save(category);
        assertFalse(category.getIsEnabled());
    }

    @Test
    void shouldReturnNothing_WhenEnableCategory() {
        String _id = "123";
        Category category = new Category("cat1", "category");
        category.set_id(_id);
        category.setIsEnabled(false);

        when(categoryRepository.findCategoryByCategoryId(anyString())).thenReturn(Optional.of(category));

        categoryService.enableCategory(_id);

        verify(categoryRepository, times(1)).save(category);
        assertTrue(category.getIsEnabled());
    }

    @Test
    void shouldReturnAllCategories_WhenGetAllCategories(){
        List<Category> categories = new ArrayList<>();
        Category category = new Category("cat1", "category");
        categories.add(category);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(result, categories);
        verify(categoryRepository, times(1)).findAll();
    }
}