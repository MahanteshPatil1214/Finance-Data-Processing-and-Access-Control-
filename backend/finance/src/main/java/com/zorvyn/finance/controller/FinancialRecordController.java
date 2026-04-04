package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordFilterDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.DTOs.FinancialRecordResponseDTO;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.UserRepository;
import com.zorvyn.finance.service.FinancialRecordService;
import com.zorvyn.finance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST controller for managing financial records.
 * Provides endpoints for CRUD operations and summary dashboard APIs.
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "Endpoints for managing financial data")
public class FinancialRecordController {

    private final UserRepository userRepository;
    private final FinancialRecordService recordService;
    private final UserService userService;

    /**
     * Retrieves the dashboard summary of financial records.
     *
     * @return the dashboard summary data
     */
    @Operation(summary = "Get dashboard summary", description = "Provides a summary of key financial metrics.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        return ResponseEntity.ok(recordService.getSummary());
    }

    @Operation(summary = "Get category breakdown", description = "Provides spending per category for the current month.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    @GetMapping("/summary/category-breakdown")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryBreakdown(java.security.Principal principal) {
        // 1. Get the email from the logged-in session
        String email = principal.getName();

        // 2. Find your ACTUAL database user entity by email
        // NOTE: You must have userRepository injected in this controller for this to work
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 3. Pass your actual User entity to the service
        return ResponseEntity.ok(userService.getMonthlyCategoryBreakdown(user));
    }

    /**
     * Retrieves a paginated list of all financial records.
     *
     * @param filter the optional filter criteria for the records
     * @param pageable pagination and sorting details
     * @return a page of financial records
     */
    @Operation(summary = "Get all financial records", description = "Retrieves a paginated list of all records with optional filtering. Requires ADMIN or ANALYST role.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @GetMapping
    public ResponseEntity<Page<FinancialRecordResponseDTO>> getAllRecords(
            @ParameterObject @ModelAttribute FinancialRecordFilterDTO filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(recordService.getAllRecords(filter, pageable));
    }

    /**
     * Creates a new financial record.
     *
     * @param request the details of the record to create
     * @return a success message
     */
    @Operation(summary = "Create a financial record", description = "Adds a new financial record to the system. Requires ADMIN role.")
    @ApiResponse(responseCode = "201", description = "Financial record created successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createRecord(@Valid @RequestBody FinancialRecordRequestDTO request) {
        recordService.saveRecord(request);
        return new ResponseEntity<>("Financial record created successfully", HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific financial record by its ID.
     *
     * @param displayId the display ID of the record
     * @return the financial record details
     */
    @Operation(summary = "Get a financial record by ID", description = "Retrieves details of a specific financial record. Requires ADMIN or ANALYST role.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @GetMapping("/{displayId}")
    public ResponseEntity<FinancialRecordResponseDTO> getRecordById(@PathVariable String displayId) {
        return ResponseEntity.ok(recordService.getRecordById(displayId));
    }

    /**
     * Updates an existing financial record.
     *
     * @param displayId the display ID of the record to update
     * @param request the updated record data
     * @return the updated financial record details
     */
    @Operation(summary = "Update a financial record", description = "Modifies an existing financial record. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{displayId}")
    public ResponseEntity<FinancialRecordResponseDTO> updateRecord(
            @PathVariable String displayId,
            @Valid @RequestBody FinancialRecordRequestDTO request) {
        return ResponseEntity.ok(recordService.updateRecord(displayId, request));
    }

    /**
     * Deletes a financial record.
     *
     * @param displayId the display ID of the record to delete
     * @return a response indicating successful deletion
     */
    @Operation(summary = "Delete a financial record", description = "Removes a standard financial record from the database. Requires ADMIN role.")
    @ApiResponse(responseCode = "204", description = "Financial record deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{displayId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String displayId) {
        recordService.deleteRecord(displayId);
        return ResponseEntity.noContent().build();
    }
}
