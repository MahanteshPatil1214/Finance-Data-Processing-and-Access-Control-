package com.zorvyn.finance.controller;

import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for retrieving metadata.
 * Provides endpoints for fetching categories and transaction types.
 */
@RestController
@RequestMapping("/api/meta")
@Tag(name = "Metadata", description = "Endpoints for retrieving application metadata")
public class MetaDataController {

    /**
     * Retrieves all available categories.
     *
     * @return a list of category names
     */
    @Operation(summary = "Get categories", description = "Retrieves a list of all defined categories.")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = Arrays.stream(Category.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves all available transaction types.
     *
     * @return a list of transaction type names
     */
    @Operation(summary = "Get transaction types", description = "Retrieves a list of all defined transaction types.")
    @GetMapping("/transaction-types")
    public ResponseEntity<List<String>> getTransactionTypes() {
        List<String> types = Arrays.stream(TransactionType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }
}
