package com.springapi.springapitechnicaltest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.services.CategoryService;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class CategoryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtServiceImpl jwtService;

    @MockBean
    CategoryService categoryService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveCategoryNoAuth() throws Exception{
        Category category = new Category("1", "TestCategory","Description");
        category.set_id("1");

        when(categoryService.saveCategory(eq(category)))
                .thenReturn(category);

        mockMvc.perform(post("/api/v1/category/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(category)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void saveCategoryRoleUser() throws Exception{
        Category category = new Category("1", "TestCategory","Description");
        category.set_id("1");

        when(categoryService.saveCategory(eq(category)))
                .thenReturn(category);

        mockMvc.perform(post("/api/v1/category/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(category)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveCategory() throws Exception{
        Category category = new Category("1", "TestCategory","Description");
        category.set_id("1");

        when(categoryService.saveCategory(eq(category)))
                .thenReturn(category);

        mockMvc.perform(post("/api/v1/category/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(category)))
                .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/v1/category/get/1"));

        verify(categoryService, times(1)).saveCategory(eq(category));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getCategoryByCategoryId() throws Exception{
        String categoryId = "1";
        Category sampleCategory = new Category("1","category","description");

        when(categoryService.getCategoryByCategoryId(eq(categoryId)))
                .thenReturn(sampleCategory);

        mockMvc.perform(get("/api/v1/category/get/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId", is(categoryId)))
                .andExpect(jsonPath("$.name", is("category")))
                .andExpect(jsonPath("$.description", is("description")));

        verify(categoryService, times(1)).getCategoryByCategoryId(eq(categoryId));
    }

    @Test
    void getCategoryByCategoryIdNoAuth() throws Exception{
        String categoryId = "1";
        Category sampleCategory = new Category("1","category","description");

        when(categoryService.getCategoryByCategoryId(eq(categoryId)))
                .thenReturn(sampleCategory);

        mockMvc.perform(get("/api/v1/category/get/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getCategoryByCategoryIdRoleUser() throws Exception{
        String categoryId = "1";
        Category sampleCategory = new Category("1","category","description");

        when(categoryService.getCategoryByCategoryId(eq(categoryId)))
                .thenReturn(sampleCategory);

        mockMvc.perform(get("/api/v1/category/get/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateCategory() throws Exception{
        Category sampleCategory = new Category("1", "category", "description");

        mockMvc.perform(put("/api/v1/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleCategory)))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).updateCategory(eq(sampleCategory));
    }

    @Test
    @WithMockUser
    void updateCategoryRoleUser() throws Exception{
        Category sampleCategory = new Category("1", "category", "description");

        mockMvc.perform(put("/api/v1/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleCategory)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCategoryNoAuth() throws Exception{
        Category sampleCategory = new Category("1", "category", "description");

        mockMvc.perform(put("/api/v1/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleCategory)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteCategory() throws Exception{
        String categoryId = "123";

        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(eq(categoryId));
    }

    @Test
    @WithMockUser
    void deleteCategoryRoleUser() throws Exception{
        String categoryId = "123";

        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategoryNoAuth() throws Exception{
        String categoryId = "123";

        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void disableCategory() throws Exception{
        String categoryId = "123";

        doNothing().when(categoryService).disableCategory(eq(categoryId));

        mockMvc.perform(patch("/api/v1/category/disable/{categoryId}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).disableCategory(eq(categoryId));
    }

    @Test
    @WithMockUser
    void disableCategoryRoleUser() throws Exception{
        String categoryId = "123";

        doNothing().when(categoryService).disableCategory(eq(categoryId));

        mockMvc.perform(patch("/api/v1/category/disable/{categoryId}", categoryId))
                .andExpect(status().isForbidden());
    }

    @Test
    void disableCategoryNoAuth() throws Exception{
        String categoryId = "123";

        doNothing().when(categoryService).disableCategory(eq(categoryId));

        mockMvc.perform(patch("/api/v1/category/disable/{categoryId}", categoryId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void enableCategory() throws Exception{
        String categoryId = "123";

        doNothing().when(categoryService).enableCategory(eq(categoryId));

        mockMvc.perform(patch("/api/v1/category/enable/{categoryId}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).enableCategory(eq(categoryId));
    }

    @Test
    @WithMockUser
    void enableCategoryRoleUser() throws Exception{
        String categoryId = "123";

        doNothing().when(categoryService).enableCategory(eq(categoryId));

        mockMvc.perform(patch("/api/v1/category/enable/{categoryId}", categoryId))
                .andExpect(status().isForbidden());
    }

    @Test
    void enableCategoryNoAuth() throws Exception{
        String categoryId = "123";

        doNothing().when(categoryService).enableCategory(eq(categoryId));

        mockMvc.perform(patch("/api/v1/category/enable/{categoryId}", categoryId))
                .andExpect(status().isForbidden());
    }

    private static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}