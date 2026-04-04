package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Category;
import lombok.Data;
import java.util.UUID;

@Data
public class CategoryResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean active;

    public CategoryResponseDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.active = category.isActive();
    }
}