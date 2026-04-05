package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.CategoryRequestDTO;
import com.zorvyn.finance.DTOs.CategoryResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service interface for managing the system's financial categories.
 * Handles the creation, retrieval, and availability of categories used
 * to classify financial transactions and set budgets.
 */
public interface CategoryService {

    /**
     * Creates and persists a new category entity.
     * * @param name        The unique name of the category (e.g., "Food", "Rent").
     * @param description A brief explanation of what transactions fall under this category.
     * @return The persisted {@link Category} entity.
     */
    Category createCategory(String name, String description);

    /**
     * Retrieves all categories that are currently marked as active.
     * Used to populate dropdowns and selection menus in the user interface.
     * * @return A list of active {@link Category} entities.
     */
    List<Category> getAllActiveCategories();

    /**
     * Finds a specific category by its name, ignoring case sensitivity.
     * * @param name The name of the category to search for.
     * @return The matching {@link Category} entity.
     * @throws ResourceNotFoundException if no category with the given name exists.
     */
    Category getCategoryByName(String name);

    /**
     * Maps a category request DTO to an entity, saves it, and returns a response DTO.
     * This is the primary method used by the REST controllers.
     * * @param request DTO containing category details from the API client.
     * @return A {@link CategoryResponseDTO} representing the saved category.
     */
    CategoryResponseDTO save(CategoryRequestDTO request);
}
