package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.BudgetAlertDTO;
import com.zorvyn.finance.DTOs.BudgetRequestDTO;
import com.zorvyn.finance.DTOs.BudgetResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.Budget;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.BudgetRepository;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BudgetResponseDTO setBudget(String userEmail, BudgetRequestDTO request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Budget budget;
        if (request.getCategory() != null) {
            budget = budgetRepository.findByUserAndCategoryAndYearAndMonth(user, request.getCategory(), request.getYear(), request.getMonth())
                    .orElse(new Budget());
        } else {
            budget = budgetRepository.findByUserAndCategoryAndYearAndMonth(user, null, request.getYear(), request.getMonth())
                    .orElse(new Budget());
        }

        budget.setUser(user);
        budget.setCategory(request.getCategory());
        budget.setLimitAmount(request.getLimitAmount());
        budget.setYear(request.getYear());
        budget.setMonth(request.getMonth());

        return new BudgetResponseDTO(budgetRepository.save(budget));
    }

    @Override
    public List<BudgetResponseDTO> getBudgets(String userEmail, int year, int month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return budgetRepository.findByUserAndYearAndMonth(user, year, month)
                .stream()
                .map(BudgetResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetAlertDTO> checkBudgetAlerts(String userEmail, int year, int month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Budget> budgets = budgetRepository.findByUserAndYearAndMonth(user, year, month);
        List<BudgetAlertDTO> alerts = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        for (Budget budget : budgets) {
            BigDecimal currentSpend;
            if (budget.getCategory() == null) {
                currentSpend = financialRecordRepository.sumTotalExpenses(user, startDate, endDate);
            } else {
                currentSpend = financialRecordRepository.sumExpensesByCategory(user, startDate, endDate, budget.getCategory());
            }

            if (currentSpend == null) {
                currentSpend = BigDecimal.ZERO;
            }

            BigDecimal utilization = BigDecimal.ZERO;
            if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
                utilization = currentSpend.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }

            // 80% Threshold
            if (utilization.compareTo(new BigDecimal("80.00")) >= 0) {
                String msg = utilization.compareTo(new BigDecimal("100.00")) >= 0
                        ? "You have exceeded your budget!"
                        : "Warning: You have used " + utilization.setScale(2, RoundingMode.HALF_UP) + "% of your budget.";

                alerts.add(new BudgetAlertDTO(
                        budget.getCategory(),
                        budget.getLimitAmount(),
                        currentSpend,
                        utilization,
                        msg
                ));
            }
        }

        return alerts;
    }
}
