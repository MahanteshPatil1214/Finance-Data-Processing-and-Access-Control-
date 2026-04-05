package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.AnomalyAlertDTO;
import com.zorvyn.finance.DTOs.HighSpenderDTO;
import com.zorvyn.finance.DTOs.TrendAnalysisDTO;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalystFinancialServiceImpl implements AnalystFinancialService{

    @Autowired
    private FinancialRecordRepository repository;

    /**
     * Aggregates global platform metrics, including total monetary volume and
     * transaction distribution by category.
     * * <p>The method performs the following:
     * 1. Sums all transaction amounts across the entire platform.
     * 2. Maps raw category statistics from the repository into a readable Map
     * where the key is the Category Name and the value is the count of records.
     * * @return A {@link Map} containing:
     * - "totalPlatformVolume": The sum of all transactions (BigDecimal)
     * - "categoryDistribution": A nested Map of category names to counts (Map<String, Long>)
     */
    @Override
    public Map<String, Object> getGlobalPlatformStats() {
        BigDecimal totalVolume = repository.sumAllTransactions();
        List<Object[]> categoryData = repository.getCategoryWiseStats();

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalPlatformVolume", totalVolume != null ? totalVolume : BigDecimal.ZERO);

        // Map Category stats to a readable format
        Map<String, Long> categoryCounts = categoryData.stream()
                .collect(java.util.stream.Collectors.toMap(
                        obj -> obj[0].toString(), // Category Name
                        obj -> (Long) obj[1]      // Count
                ));
        stats.put("categoryDistribution", categoryCounts);

        return stats;
    }

    /**
     * Identifies users whose cumulative transaction volume exceeds a specified threshold.
     * * <p>This method processes raw results from the database (Object arrays) and
     * transforms them into a list of HighSpenderDTOs. It is typically used by
     * analysts to monitor high-value accounts or detect unusual spending patterns
     * that require manual review.
     * * @param threshold The minimum total spending amount (BigDecimal) required to
     * flag a user as a "high spender".
     * @return A {@link List} of {@link HighSpenderDTO} objects containing user
     * identifiers and their corresponding total volume.
     */
    @Override
    public List<HighSpenderDTO> getHighSpenderAlerts(BigDecimal threshold) {
        List<Object[]> results = repository.findHighSpenders(threshold);
        return results.stream()
                .map(obj -> new HighSpenderDTO((String) obj[0], (BigDecimal) obj[1]))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Calculates the platform growth by comparing two 30-day windows.
     * Uses HALF_UP rounding for precision in percentage calculations.
     */
    @Override
    public TrendAnalysisDTO getMonthOverMonthTrend() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        LocalDateTime sixtyDaysAgo = now.minusDays(60);

        // 1. Get Totals for both periods
        BigDecimal currentTotal = repository.sumVolumeInRange(thirtyDaysAgo, now);
        BigDecimal previousTotal = repository.sumVolumeInRange(sixtyDaysAgo, thirtyDaysAgo);

        // Handle nulls
        currentTotal = (currentTotal != null) ? currentTotal : BigDecimal.ZERO;
        previousTotal = (previousTotal != null) ? previousTotal : BigDecimal.ZERO;

        // 2. Calculate Percentage Change
        Double pctChange = 0.0;
        String status = "STABLE";

        if (previousTotal.compareTo(BigDecimal.ZERO) > 0) {
            pctChange = ((currentTotal.subtract(previousTotal))
                    .divide(previousTotal, 4, java.math.RoundingMode.HALF_UP))
                    .doubleValue() * 100;
        }

        if (pctChange > 0.5) status = "UP";
        else if (pctChange < -0.5) status = "DOWN";

        return new TrendAnalysisDTO(currentTotal, previousTotal, pctChange, status);
    }

    /**
     * Detects financial outliers by comparing current expenses against historical averages.
     * * Logic:
     * 1. Filters for EXPENSE types only.
     * 2. Calculates a multiplier (Transaction Amount / User Average).
     * 3. Flags transactions that are 3x (300%) or higher than the average.
     * * @return List of {@link AnomalyAlertDTO} containing spike details.
     */
    @Override
    public List<AnomalyAlertDTO> getRecentAnomalies() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<FinancialRecord> recentRecords = repository.findAllByTransactionDateAfterAndIsDeletedFalse(oneDayAgo);


        List<AnomalyAlertDTO> anomalies = new ArrayList<>();

        for (FinancialRecord record : recentRecords) {
            // 1. Logic Check: Only analyze Expenses
            if (record.getType() != TransactionType.EXPENSE) continue;

            BigDecimal avg = repository.getAverageExpenseForUser(record.getCreator().getId());

            if (avg != null && avg.compareTo(BigDecimal.ZERO) > 0) {
                // 2. Calculation: How many times larger is this record than the average?
                BigDecimal multiplier = record.getAmount().divide(avg, 2, RoundingMode.HALF_UP);


                // 3. Trigger: Flag if it's 3x or more (3.0).
                // Using 3.0 instead of 5.0 makes it easier to catch spikes like 1000 vs 200.
                if (multiplier.compareTo(new BigDecimal("3.0")) >= 0) {

                    // 4. FIX: Create the DTO and actually ADD it to the list!
                    AnomalyAlertDTO alert = new AnomalyAlertDTO();
                    alert.setDisplayId(record.getDisplayId());
                    alert.setUserEmail(record.getCreator().getEmail());
                    double percentage = multiplier.multiply(new BigDecimal("100")).doubleValue();
                    alert.setSpikePercentage(percentage);
                    alert.setAmount(record.getAmount());
                    alert.setUserAverage(avg);
                    alert.setReason("Sudden Spike: This transaction is " + multiplier + "x higher than the user's average.");

                    anomalies.add(alert);
                }
            }
        }

        return anomalies;
    }
}
