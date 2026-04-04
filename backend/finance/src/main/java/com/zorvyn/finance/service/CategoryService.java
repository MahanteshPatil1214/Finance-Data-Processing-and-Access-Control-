package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.CategoryRequestDTO;
import com.zorvyn.finance.DTOs.CategoryResponseDTO;
import com.zorvyn.finance.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    Category createCategory(String name, String description);
    List<Category> getAllActiveCategories();
    Category getCategoryByName(String name);

    CategoryResponseDTO save(CategoryRequestDTO request);
}
