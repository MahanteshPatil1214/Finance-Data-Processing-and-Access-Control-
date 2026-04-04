package com.zorvyn.finance.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TrendAnalysisDTO {
    private BigDecimal currentPeriodTotal;
    private BigDecimal previousPeriodTotal;
    private Double percentageChange;
    private String trendStatus; // "UP", "DOWN", or "STABLE"
}
