package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetRequestDTO {
    private Category category; // Can be null for overall budget

    @NotNull(message = "Limit amount is required")
    @Min(value = 0, message = "Limit amount cannot be negative")
    private BigDecimal limitAmount;

    @Min(value = 1, message = "Month must be between 1 and 12")
    private int month;

    @Min(value = 2000, message = "Year must be valid")
    private int year;
}
