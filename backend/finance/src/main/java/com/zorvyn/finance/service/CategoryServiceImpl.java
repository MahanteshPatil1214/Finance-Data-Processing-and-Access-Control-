package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.CategoryRequestDTO;
import com.zorvyn.finance.DTOs.CategoryResponseDTO;
import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

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

    @Override
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllByActiveTrue();
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }

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
