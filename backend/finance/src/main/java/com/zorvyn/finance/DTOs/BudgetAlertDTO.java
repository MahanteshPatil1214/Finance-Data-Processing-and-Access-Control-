package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlertDTO {
    private Category category;
    private BigDecimal limitAmount;
    private BigDecimal currentSpend;
    private BigDecimal utilizationPercentage;
    private String alertMessage;
}
