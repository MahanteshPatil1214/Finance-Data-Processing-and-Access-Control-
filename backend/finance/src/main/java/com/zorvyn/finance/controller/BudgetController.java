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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Endpoints for managing budgets and alerts")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "Set a budget", description = "Create or update a budget for a specific category and month.")
    @PostMapping
    public ResponseEntity<BudgetResponseDTO> setBudget(
            Authentication authentication,
            @Valid @RequestBody BudgetRequestDTO request) {
        BudgetResponseDTO response = budgetService.setBudget(authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get budgets", description = "Retrieve all budgets for a given year and month. Defaults to current month if not provided.")
    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getBudgets(
            Authentication authentication,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        int y = (year != null) ? year : LocalDate.now().getYear();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        
        return ResponseEntity.ok(budgetService.getBudgets(authentication.getName(), y, m));
    }

    @Operation(summary = "Get budget alerts", description = "Check if any budgets are close to or exceeding their limits for the specified month.")
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
