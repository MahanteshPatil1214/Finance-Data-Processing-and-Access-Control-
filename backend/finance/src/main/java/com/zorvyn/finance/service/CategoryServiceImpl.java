package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.CategoryRequestDTO;
import com.zorvyn.finance.DTOs.CategoryResponseDTO;
import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link CategoryService} providing the business logic for
 * financial category management.
 * * <p>Key Features:
 * <ul>
 * <li>Enforces unique category names (case-insensitive).</li>
 * <li>Standardizes all category names to UPPERCASE for consistency.</li>
 * <li>Supports logical deletion via the 'active' status flag.</li>
 * </ul>
 * </p>
 */
@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Internal method to create a new category.
     * Enforces the business rule that names must be unique and are stored in uppercase.
     * * @param name        The display name of the category.
     * @param description A brief description of the category's purpose.
     * @return The persisted {@link Category} entity.
     * @throws RuntimeException if a category with the same name already exists.
     */
    @Override
    public Category createCategory(String name, String description) {
        // Business Rule: Prevent duplicate names
        if (categoryRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new RuntimeException("Category with name " + name + " already exists!");
        }

        Category category = Category.builder()
                .name(name.toUpperCase()) // Keep names consistent
                .description(description)
                .active(true)
                .build();

        return categoryRepository.save(category);
    }

    /**
     * Retrieves all categories available for selection (active = true).
     * @return A list of active {@link Category} entities.
     */
    @Override
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllByActiveTrue();
    }

    /**
     * Retrieves a specific category by its unique name, ignoring case sensitivity.
     * <p>
     * This method is commonly used during data initialization or when processing
     * incoming financial records to link them to the correct category entity.
     * </p>
     * * @param name The name of the category to search for (e.g., "FOOD" or "food").
     * @return The matching {@link Category} entity.
     * @throws RuntimeException if no category exists with the provided name.
     */
    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }

    /**
     * Maps a {@link CategoryRequestDTO} to an entity and persists it.
     * Standardizes the name to uppercase before checking for duplicates.
     * * @param request The incoming data transfer object from the API.
     * @return A {@link CategoryResponseDTO} containing the saved category details.
     * @throws RuntimeException if the category name is already in use.
     */
    @Override
    public CategoryResponseDTO save(CategoryRequestDTO request) {
        // 1. Check for duplicates
        if (categoryRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new RuntimeException("Category already exists!");
        }

        // 2. Map DTO to Entity
        Category category = Category.builder()
                .name(request.getName().toUpperCase())
                .description(request.getDescription())
                .active(true)
                .build();

        // 3. Save and Map back to ResponseDTO
        return new CategoryResponseDTO(categoryRepository.save(category));
    }
}
