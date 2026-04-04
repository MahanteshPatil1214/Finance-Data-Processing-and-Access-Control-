package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.AnomalyAlertDTO;
import com.zorvyn.finance.DTOs.HighSpenderDTO;
import com.zorvyn.finance.DTOs.TrendAnalysisDTO;
import com.zorvyn.finance.service.AnalystFinancialService;
import com.zorvyn.finance.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analyst")
@RequiredArgsConstructor
@Tag(name = "Analyst Operations", description = "Specialized endpoints for data analysis and platform monitoring")
public class AnalystController {

    @Autowired
    private AnalystFinancialService recordService;

    @Operation(summary = "Get global platform stats", description = "Aggregated data across all users. Requires ANALYST or ADMIN role.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @GetMapping("/stats/global")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        return ResponseEntity.ok(recordService.getGlobalPlatformStats());
    }

    @Operation(summary = "Find high spenders", description = "Identifies users who have spent more than the threshold. Useful for fraud detection.")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/reports/high-spenders")
    public ResponseEntity<List<HighSpenderDTO>> getHighSpenders(
            @RequestParam(defaultValue = "50000") BigDecimal threshold) {
        return ResponseEntity.ok(recordService.getHighSpenderAlerts(threshold));
    }

    @Operation(summary = "Get MoM Trend Analysis", description = "Compares the last 30 days of platform volume to the previous 30 days.")
    @PreAuthorize("hasRole('ANALYST')")
    @GetMapping("/trends/mom")
    public ResponseEntity<TrendAnalysisDTO> getMoMTrend() {
        return ResponseEntity.ok(recordService.getMonthOverMonthTrend());
    }

    @Operation(summary = "Detect financial anomalies", description = "Flags transactions 500% higher than the user's historical average.")
    @PreAuthorize("hasAnyAuthority('ANALYST', 'ROLE_ANALYST')")
    @GetMapping("/alerts")
    public ResponseEntity<List<AnomalyAlertDTO>> getAlerts() {
        List<AnomalyAlertDTO> alerts = recordService.getRecentAnomalies();
        return ResponseEntity.ok(alerts);
}}