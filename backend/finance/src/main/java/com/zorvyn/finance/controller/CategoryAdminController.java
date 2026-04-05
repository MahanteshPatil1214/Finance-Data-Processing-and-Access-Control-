package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.CategoryRequestDTO;
import com.zorvyn.finance.DTOs.CategoryResponseDTO;

import com.zorvyn.finance.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Administrative controller for managing financial categories.
 * This controller is restricted to users with the 'ADMIN' role and allows for
 * the creation and modification of the system-wide category registry.
 */
@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin Category Management")
public class CategoryAdminController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Creates a new financial category for the platform.
     * Restricted to users with Administrative privileges.
     * * @param request DTO containing the category name and optional description.
     * @return {@link CategoryResponseDTO} containing the created category details.
     * @status 201 Created if the category is successfully persisted.
     * @auth Requires 'ROLE_ADMIN'
     */
    @Operation(summary = "Admin only: Create a new category",
            description = "Adds a new category to the system. Only accessible by administrators.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO request) {
        return new ResponseEntity<>(categoryService.save(request), HttpStatus.CREATED);
    }
}