package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.AnomalyAlertDTO;
import com.zorvyn.finance.DTOs.HighSpenderDTO;
import com.zorvyn.finance.DTOs.TrendAnalysisDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service interface for high-level financial data analysis.
 * Provides methods for platform-wide statistics, trend forecasting, 
 * and automated fraud/anomaly detection.
 */
public interface AnalystFinancialService {

    /**
     * Aggregates total transaction volume and category distribution across the platform.
     * @return A map containing 'totalPlatformVolume' and 'categoryDistribution'.
     */
    Map<String, Object> getGlobalPlatformStats();

    /**
     * Identifies users whose total spending exceeds a specific limit.
     * @param threshold The minimum amount to qualify as a high spender.
     * @return A list of users and their total spent amounts.
     */
    List<HighSpenderDTO> getHighSpenderAlerts(BigDecimal threshold);

    /**
     * Compares the financial volume of the last 30 days against the 30 days prior.
     * @return TrendAnalysisDTO containing totals, percentage change, and status.
     */
    TrendAnalysisDTO getMonthOverMonthTrend();

    /**
     * Scans recent transactions to find spikes in user spending.
     * @return A list of alerts for transactions significantly higher than a user's average.
     */
    List<AnomalyAlertDTO> getRecentAnomalies();
}