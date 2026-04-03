package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinancialRecordFilterDTO {
    private TransactionType type;
    private Category category;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
