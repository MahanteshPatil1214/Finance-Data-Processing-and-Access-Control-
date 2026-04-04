package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.HighSpenderDTO;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class AnalystFinancialServiceImpl implements AnalystFinancialService{

    @Autowired
    private FinancialRecordRepository repository;

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

    @Override
    public List<HighSpenderDTO> getHighSpenderAlerts(BigDecimal threshold) {
        List<Object[]> results = repository.findHighSpenders(threshold);
        return results.stream()
                .map(obj -> new HighSpenderDTO((String) obj[0], (BigDecimal) obj[1]))
                .collect(java.util.stream.Collectors.toList());
    }
}
