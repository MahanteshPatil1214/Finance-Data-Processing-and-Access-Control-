package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.DTOs.FinancialRecordResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing the lifecycle of financial records.
 * Provides capabilities for transaction CRUD operations, advanced filtering,
 * and generating high-level dashboard metrics for the platform.
 */
public interface FinancialRecordService {

    /**
     * Generates a comprehensive summary of financial health metrics.
     * Aggregates data such as total balance, income vs. expense ratios,
     * and recent transaction volume for the dashboard view.
     * * @return A {@link DashboardSummaryDTO} containing key performance indicators.
     */
    DashboardSummaryDTO getSummary();

    /**
     * Persists a new financial transaction to the database.
     * Validates category existence and maps the incoming request to the
     * internal FinancialRecord entity.
     * * @param request DTO containing the amount, type, category, and date of the transaction.
     */
    void saveRecord(FinancialRecordRequestDTO request);

    /**
     * Retrieves a paginated and filtered list of financial records.
     * Supports dynamic filtering by date ranges, transaction types (INCOME/EXPENSE),
     * and specific categories.
     * * @param filter   An object containing optional search criteria.
     * @param pageable Pagination parameters (page number, size, and sorting).
     * @return A {@link Page} of {@link FinancialRecordResponseDTO} matching the criteria.
     */
    Page<FinancialRecordResponseDTO> getAllRecords(
            com.zorvyn.finance.DTOs.FinancialRecordFilterDTO filter,
            Pageable pageable);

    /**
     * Retrieves the detailed information of a specific financial record.
     * * @param displayId The public-facing unique identifier of the record.
     * @return The corresponding {@link FinancialRecordResponseDTO}.
     * @throws ResourceNotFoundException if the record ID does not exist.
     */
    FinancialRecordResponseDTO getRecordById(String displayId);

    /**
     * Updates an existing financial record with new information.
     * Allows for correction of amounts, categories, or descriptions while
     * maintaining the original record's unique identity.
     * * @param displayId The unique identifier of the record to be modified.
     * @param request   The updated data to be applied to the record.
     * @return The updated {@link FinancialRecordResponseDTO}.
     * @throws ResourceNotFoundException if the record ID is invalid.
     */
    FinancialRecordResponseDTO updateRecord(String displayId, FinancialRecordRequestDTO request);

    /**
     * Removes a financial record from the system.
     * Depending on the implementation, this may perform a hard delete or
     * a logical "soft delete" by updating a status flag.
     * * @param displayId The unique identifier of the record to be deleted.
     */
    void deleteRecord(String displayId);
}
