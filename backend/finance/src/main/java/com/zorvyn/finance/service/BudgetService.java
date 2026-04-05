package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.BudgetAlertDTO;
import com.zorvyn.finance.DTOs.BudgetRequestDTO;
import com.zorvyn.finance.DTOs.BudgetResponseDTO;

import java.util.List;

/**
 * Service interface for managing user-defined budgets and financial thresholds.
 * Provides capabilities to establish monthly spending limits by category
 * and evaluate real-time spending against those limits to generate alerts.
 */
public interface BudgetService {

    /**
     * Creates a new budget or updates an existing one for a specific category and month.
     * If a budget already exists for the given user, category, and period, it will be overwritten.
     * * @param userEmail The unique email of the authenticated user.
     * @param request    The {@link BudgetRequestDTO} containing the limit, category, and period.
     * @return A {@link BudgetResponseDTO} representing the persisted budget state.
     */
    BudgetResponseDTO setBudget(String userEmail, BudgetRequestDTO request);

    /**
     * Retrieves all active budgets for a user within a specific calendar month.
     * * @param userEmail The unique email of the authenticated user.
     * @param year      The calendar year (e.g., 2026).
     * @param month     The calendar month (1-12).
     * @return A list of {@link BudgetResponseDTO} objects for the specified period.
     */
    List<BudgetResponseDTO> getBudgets(String userEmail, int year, int month);

    /**
     * Analyzes current spending patterns against established budgets to identify
     * categories that are nearing or have exceeded their limits.
     * * @param userEmail The unique email of the authenticated user.
     * @param year      The calendar year to analyze.
     * @param month     The calendar month to analyze.
     * @return A list of {@link BudgetAlertDTO} containing the budget limit,
     * current spend, and the status of the alert.
     */
    List<BudgetAlertDTO> checkBudgetAlerts(String userEmail, int year, int month);
}
