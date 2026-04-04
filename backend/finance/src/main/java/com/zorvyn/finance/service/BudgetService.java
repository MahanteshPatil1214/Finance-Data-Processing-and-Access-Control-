package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.BudgetAlertDTO;
import com.zorvyn.finance.DTOs.BudgetRequestDTO;
import com.zorvyn.finance.DTOs.BudgetResponseDTO;

import java.util.List;

public interface BudgetService {
    BudgetResponseDTO setBudget(String userEmail, BudgetRequestDTO request);
    List<BudgetResponseDTO> getBudgets(String userEmail, int year, int month);
    List<BudgetAlertDTO> checkBudgetAlerts(String userEmail, int year, int month);
}
