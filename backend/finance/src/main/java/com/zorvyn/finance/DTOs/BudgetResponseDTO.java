package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Budget;
import com.zorvyn.finance.model.Category;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetResponseDTO {
    private String displayId;
    private Category category;
    private BigDecimal limitAmount;
    private int month;
    private int year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BudgetResponseDTO(Budget budget) {
        this.displayId = budget.getDisplayId();
        this.category = budget.getCategory();
        this.limitAmount = budget.getLimitAmount();
        this.month = budget.getMonth();
        this.year = budget.getYear();
        this.createdAt = budget.getCreatedAt();
        this.updatedAt = budget.getUpdatedAt();
    }
}
