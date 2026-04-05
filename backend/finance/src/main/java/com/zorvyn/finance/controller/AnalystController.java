package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.AnomalyAlertDTO;
import com.zorvyn.finance.DTOs.HighSpenderDTO;
import com.zorvyn.finance.DTOs.TrendAnalysisDTO;
import com.zorvyn.finance.service.AnalystFinancialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller providing administrative and analytical insights for the platform.
 * Contains endpoints for monitoring global statistics, detecting fraud/anomalies,
 * and performing trend analysis across all financial records.
 */
@RestController
@RequestMapping("/api/analyst")
@RequiredArgsConstructor
@Tag(name = "Analyst Operations", description = "Specialized endpoints for data analysis and platform monitoring")
public class AnalystController {

    private final AnalystFinancialService recordService;

    /**
     * Retrieves aggregated platform-wide statistics.
     * Accessible by users with ADMIN or ANALYST roles.
     * * @return A map containing global KPIs like total volume, user count, and transaction density.
     */
    @Operation(summary = "Get global platform stats", description = "Aggregated data across all users.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @GetMapping("/stats/global")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        return ResponseEntity.ok(recordService.getGlobalPlatformStats());
    }

    /**
     * Identifies users whose spending exceeds a specific threshold.
     * Primarily used for identifying premium users or potential fraud cases.
     * * @param threshold The decimal value above which a user is flagged as a high spender (default 50,000).
     * @return A list of HighSpenderDTOs containing user details and total expenditure.
     */
    @Operation(summary = "Find high spenders", description = "Identifies users who have spent more than the threshold.")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/reports/high-spenders")
    public ResponseEntity<List<HighSpenderDTO>> getHighSpenders(
            @RequestParam(defaultValue = "50000") BigDecimal threshold) {
        return ResponseEntity.ok(recordService.getHighSpenderAlerts(threshold));
    }

    /**
     * Performs Month-Over-Month (MoM) growth and volume analysis.
     * * @return TrendAnalysisDTO containing comparison metrics between the current and previous 30-day periods.
     */
    @Operation(summary = "Get MoM Trend Analysis", description = "Compares the last 30 days of platform volume to the previous 30 days.")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/trends/mom")
    public ResponseEntity<TrendAnalysisDTO> getMoMTrend() {
        return ResponseEntity.ok(recordService.getMonthOverMonthTrend());
    }

    /**
     * Detects unusual transaction patterns (Anomalies).
     * Flags transactions that are significantly higher (e.g., 500%) than a user's average.
     * * @return A list of AnomalyAlertDTOs flagging suspicious financial activities.
     */
    @Operation(summary = "Detect financial anomalies", description = "Flags transactions 500% higher than the user's historical average.")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/alerts")
    public ResponseEntity<List<AnomalyAlertDTO>> getAlerts() {
        List<AnomalyAlertDTO> alerts = recordService.getRecentAnomalies();
        return ResponseEntity.ok(alerts);
    }
}