package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    @Override
    public DashboardSummaryDTO getSummary() {
        List<FinancialRecord> records = recordRepository.findAll();

        BigDecimal totalIncome = records.stream()
                .filter(r -> r.getType() == TransactionType.INCOME)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = records.stream()
                .filter(r -> r.getType() == TransactionType.EXPENSE)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Category-wise totals
        var breakdown = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCategory().name(),
                        Collectors.mapping(FinancialRecord::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return new DashboardSummaryDTO(
                totalIncome,
                totalExpenses,
                totalIncome.subtract(totalExpenses),
                breakdown
        );
    }

    @Override
    public void saveRecord(FinancialRecordRequestDTO request) {
            FinancialRecord record = new FinancialRecord();
            record.setAmount(request.getAmount());
            record.setType(request.getType());
            record.setCategory(request.getCategory());
            record.setDescription(request.getDescription());

            // Logic: If user didn't provide a date, use current time
            record.setTransactionDate(
                    request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now()
            );

            // Logic: Generate a unique Display ID for the transaction
            String txnId = "TXN-" + System.currentTimeMillis() % 100000;
            record.setDisplayId(txnId);

            recordRepository.save(record);
    }
}
