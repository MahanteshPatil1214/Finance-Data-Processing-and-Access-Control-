package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Category;
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
    private Category category;
    private String description;
    private LocalDateTime transactionDate;

    public FinancialRecordResponseDTO(FinancialRecord record) {
        this.displayId = record.getDisplayId();
        this.amount = record.getAmount();
        this.type = record.getType();
        this.category = record.getCategory();
        this.description = record.getDescription();
        this.transactionDate = record.getTransactionDate();
    }
}
