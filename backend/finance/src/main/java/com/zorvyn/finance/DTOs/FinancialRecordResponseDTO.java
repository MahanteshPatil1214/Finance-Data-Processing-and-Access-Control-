package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FinancialRecordResponseDTO {
    private String displayId;
    private BigDecimal amount;
    private TransactionType type;

    // Updated: Returning the String name instead of the whole Entity object
    private String categoryName;

    private String description;
    private LocalDateTime transactionDate;

    public FinancialRecordResponseDTO(FinancialRecord record) {
        this.displayId = record.getDisplayId();
        this.amount = record.getAmount();
        this.type = record.getType();

        // Updated: Safely get the name from the Category entity
        if (record.getCategory() != null) {
            this.categoryName = record.getCategory().getName();
        }

        this.description = record.getDescription();
        this.transactionDate = record.getTransactionDate();
    }
}