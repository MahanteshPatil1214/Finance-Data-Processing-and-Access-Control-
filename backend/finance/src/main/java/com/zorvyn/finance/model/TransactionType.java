package com.zorvyn.finance.model;

import com.zorvyn.finance.service.AnalystFinancialService;

/**
 * Categorizes the flow of funds for a financial record.
 * <p>
 * This enum is used by the analytics engine to aggregate totals and by 
 * the budget service to determine if a transaction should be counted 
 * against a user's spending limit.
 * </p>
 */
public enum TransactionType {

    /**
     * Represents a positive inflow of funds (e.g., Salary, Dividends, Gifts).
     * These records increase the user's total platform balance.
     */
    INCOME,

    /**
     * Represents an outflow of funds (e.g., Rent, Food, Utilities).
     * These records are validated against active {@link Budget} limits 
     * and are monitored for spikes by the {@link AnalystFinancialService}.
     */
    EXPENSE
}