package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinancialRecordRequestDTO {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Type (INCOME/EXPENSE) is required")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private Category category;

    private String description;

    private LocalDateTime transactionDate; // Optional: Defaults to now in Service
}
