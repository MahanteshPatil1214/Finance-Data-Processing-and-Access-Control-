package com.zorvyn.finance.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class HighSpenderDTO {
    private String email;
    private BigDecimal totalSpent;
}