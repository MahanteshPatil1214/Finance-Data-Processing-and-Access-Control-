package com.zorvyn.finance.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyAlertDTO {
    private String displayId;
    private String userEmail;
    private BigDecimal amount;
    private BigDecimal userAverage;
    private Double spikePercentage;
    private String reason; // e.g., "Transaction is 650% above average"
}