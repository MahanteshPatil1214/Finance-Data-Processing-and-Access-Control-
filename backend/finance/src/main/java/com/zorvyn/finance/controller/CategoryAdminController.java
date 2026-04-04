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

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin Category Management")
public class CategoryAdminController {

    @Autowired
    private final CategoryService categoryService;

    @Operation(summary = "Admin only: Create a new category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO request) {
        return new ResponseEntity<>(categoryService.save(request), HttpStatus.CREATED);
    }
}