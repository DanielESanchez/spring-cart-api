package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Category;
import com.springapi.springapitechnicaltest.services.CategoryService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
public class CategoryController {
    private final CategoryService categoryService;

    @Value("${api.request.path}")
    private String ApiPath;

    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Category saved successfully", headers = {
                    @Header(name = HttpHeaders.LOCATION, schema =
                    @Schema(type = "string"),
                            description = "Path of the category created") },
                    content = { @Content(schema = @Schema) })
    })
    @PostMapping("/category/new")
    ResponseEntity<HttpHeaders> saveCategory(@RequestBody Category category) {
        Category categorySaved = categoryService.saveCategory(category);
        String location = ApiPath + "/category/get/" + categorySaved.getCategoryId();
        return ResponseEntity.created(URI.create(location)).build();
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @GetMapping("/category/get/{categoryId}")
    Category getCategoryByCategoryId(@PathVariable String categoryId) {
        return categoryService.getCategoryByCategoryId(categoryId);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Showing category list"),
    })
    @GetMapping("/category/all/get")
    List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Showing list of all categories found",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
            })
    @PutMapping("/category/update")
    ResponseEntity<HttpStatus> updateCategory(@RequestBody Category category){
        categoryService.updateCategory(category);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @DeleteMapping("/category/delete/{categoryId}")
    ResponseEntity<?> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category disabled",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @PatchMapping("/category/disable/{categoryId}")
    ResponseEntity<HttpStatus> disableCategory(@PathVariable String categoryId){
        categoryService.disableCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category enabled",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @PatchMapping("/category/enable/{categoryId}")
    ResponseEntity<HttpStatus> enableCategory(@PathVariable String categoryId){
        categoryService.enableCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
