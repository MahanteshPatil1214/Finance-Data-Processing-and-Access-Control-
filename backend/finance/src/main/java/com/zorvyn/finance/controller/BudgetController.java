package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.BudgetAlertDTO;
import com.zorvyn.finance.DTOs.BudgetRequestDTO;
import com.zorvyn.finance.DTOs.BudgetResponseDTO;
import com.zorvyn.finance.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing user budgets and financial health alerts.
 * Users can define spending limits per category and retrieve automated alerts 
 * when they approach or exceed those limits.
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Endpoints for managing budgets and alerts")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    @Autowired
    private  BudgetService budgetService;

    /**
     * Creates a new budget or updates an existing one for the authenticated user.
     * * @param authentication The security context of the logged-in user.
     * @param request        DTO containing the category ID, limit amount, and period.
     * @return {@link BudgetResponseDTO} with a HTTP 201 Created status.
     */
    @Operation(summary = "Set a budget", description = "Create or update a budget for a specific category and month.")
    @PostMapping
    public ResponseEntity<BudgetResponseDTO> setBudget(
            Authentication authentication,
            @Valid @RequestBody BudgetRequestDTO request) {
        BudgetResponseDTO response = budgetService.setBudget(authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all budgets associated with the user for a specific month and year.
     * * @param authentication The security context of the logged-in user.
     * @param year           The year to query (defaults to current year).
     * @param month          The month to query (1-12, defaults to current month).
     * @return A list of active budgets and their configurations.
     */
    @Operation(summary = "Get budgets", description = "Retrieve all budgets for a given year and month.")
    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getBudgets(
            Authentication authentication,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int y = (year != null) ? year : LocalDate.now().getYear();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();

        return ResponseEntity.ok(budgetService.getBudgets(authentication.getName(), y, m));
    }

    /**
     * Generates alerts based on current spending vs. established budget limits.
     * Useful for dashboard notifications when a user hits 80% or 100% of their limit.
     * * @param authentication The security context of the logged-in user.
     * @param year           The year to check (defaults to current).
     * @param month          The month to check (defaults to current).
     * @return A list of {@link BudgetAlertDTO}s flagging over-budget or near-limit categories.
     */
    @Operation(summary = "Get budget alerts", description = "Check if any budgets are close to or exceeding their limits.")
    @GetMapping("/alerts")
    public ResponseEntity<List<BudgetAlertDTO>> getBudgetAlerts(
            Authentication authentication,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int y = (year != null) ? year : LocalDate.now().getYear();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();

        return ResponseEntity.ok(budgetService.checkBudgetAlerts(authentication.getName(), y, m));
    }
}