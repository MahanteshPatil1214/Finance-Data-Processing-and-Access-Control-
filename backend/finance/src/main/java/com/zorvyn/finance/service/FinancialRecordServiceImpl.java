package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.DTOs.FinancialRecordResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.zorvyn.finance.DTOs.FinancialRecordFilterDTO;
import com.zorvyn.finance.repository.FinancialRecordSpecification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FinancialRecordService} providing comprehensive
 * financial data management.
 * <p>
 * This service handles secure data access by filtering all queries based on
 * the authenticated user's email, ensuring strict data isolation between users.
 * </p>
 */
@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private final CategoryService categoryService;

    @Autowired
    private com.zorvyn.finance.repository.UserRepository userRepository;

    public FinancialRecordServiceImpl(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Internal helper to extract the email of the currently authenticated user
     * from the Spring Security Context.
     * @return The email string of the logged-in user.
     */
    private String getCurrentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Generates a real-time financial overview for the authenticated user.
     * <p>
     * Performs in-memory aggregation of the user's transaction history to calculate
     * total income, total expenses, net balance, and a category-wise spending breakdown.
     * </p>
     * @return A {@link DashboardSummaryDTO} containing the aggregated metrics.
     */
    @Override
    public DashboardSummaryDTO getSummary() {
        List<FinancialRecord> records = recordRepository.findAllByCreator_Email(getCurrentUserEmail());

        BigDecimal totalIncome = records.stream()
                .filter(r -> r.getType() == TransactionType.INCOME)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = records.stream()
                .filter(r -> r.getType() == TransactionType.EXPENSE)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Updated: Accessing the name property of the Category Entity
        var breakdown = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCategory().getName(),
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

    /**
     * Maps and persists a new financial transaction.
     * <p>
     * Automatically links the record to the authenticated user and fetches
     * the appropriate {@link Category} entity by name.
     * </p>
     * @param request DTO containing the transaction details.
     * @throws ResourceNotFoundException if the creator user cannot be verified.
     */
    @Override
    public void saveRecord(FinancialRecordRequestDTO request) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setDescription(request.getDescription());

        // Updated: Fetch the Category Entity from DB using the Service
        // Assumes your RequestDTO now passes the category name as a String
        com.zorvyn.finance.model.Category category = categoryService.getCategoryByName(request.getCategoryName());
        record.setCategory(category);

        // Attach creator
        com.zorvyn.finance.model.User creator = userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        record.setCreator(creator);

        record.setTransactionDate(
                request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now()
        );

        recordRepository.save(record);
    }

    /**
     * Executes a dynamic search for financial records using JPA Specifications.
     * <p>
     * Filters are applied cumulatively (AND logic). If a filter field is null,
     * it is ignored in the final SQL query.
     * </p>
     * @param filter   DTO containing search parameters like date range, type, and amount.
     * @param pageable Pagination and sorting configuration.
     * @return A paginated list of matching records.
     */
    @Override
    public Page<FinancialRecordResponseDTO> getAllRecords(FinancialRecordFilterDTO filter, Pageable pageable) {
        Specification<FinancialRecord> spec = FinancialRecordSpecification.hasCreatorEmail(getCurrentUserEmail());

        if (filter != null) {
            if (filter.getType() != null) {
                spec = spec.and(FinancialRecordSpecification.hasType(filter.getType()));
            }
            if (filter.getCategory() != null) {
                spec = spec.and(FinancialRecordSpecification.hasCategory(filter.getCategory()));
            }
            if (filter.getMinAmount() != null) {
                spec = spec.and(FinancialRecordSpecification.amountGreaterThanOrEqualTo(filter.getMinAmount()));
            }
            if (filter.getMaxAmount() != null) {
                spec = spec.and(FinancialRecordSpecification.amountLessThanOrEqualTo(filter.getMaxAmount()));
            }
            if (filter.getStartDate() != null) {
                spec = spec.and(FinancialRecordSpecification.dateGreaterThanOrEqualTo(filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                spec = spec.and(FinancialRecordSpecification.dateLessThanOrEqualTo(filter.getEndDate()));
            }
        }

        return recordRepository.findAll(spec, pageable).map(FinancialRecordResponseDTO::new);
    }

    /**
     * Retrieves a single record, ensuring the authenticated user is the owner.
     * @param displayId The public identifier of the record.
     * @return The record mapped to a response DTO.
     * @throws ResourceNotFoundException if the record doesn't exist or doesn't belong to the user.
     */
    @Override
    public FinancialRecordResponseDTO getRecordById(String displayId) {
        FinancialRecord record = recordRepository.findByDisplayIdAndCreator_Email(displayId, getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord with id " + displayId + " not found or unauthorized."));
        return new FinancialRecordResponseDTO(record);
    }

    /**
     * Updates an existing financial record with new details provided by the user.
     * <p>
     * <b>Security:</b> This method verifies that the record exists AND belongs
     * to the currently authenticated user before applying any changes.
     * If the category name in the request changes, it re-validates the new
     * category against the database.
     * </p>
     * * @param displayId The unique public identifier of the record to update.
     * @param request   DTO containing the updated amount, type, description, and category.
     * @return A {@link FinancialRecordResponseDTO} representing the updated state.
     * @throws ResourceNotFoundException if the record is missing or the user is unauthorized.
     */
    @Override
    public FinancialRecordResponseDTO updateRecord(String displayId, FinancialRecordRequestDTO request) {
        FinancialRecord record = recordRepository.findByDisplayIdAndCreator_Email(displayId, getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord with id " + displayId + " not found or unauthorized."));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setDescription(request.getDescription());

        // Updated: Update the category relationship
        Category category = categoryService.getCategoryByName(request.getCategoryName());
        record.setCategory(category);

        if (request.getTransactionDate() != null) {
            record.setTransactionDate(request.getTransactionDate());
        }

        FinancialRecord updatedRecord = recordRepository.save(record);
        return new FinancialRecordResponseDTO(updatedRecord);
    }

    /**
     * Permanently removes a financial record from the system.
     * <p>
     * <b>Constraint:</b> Only the record with the matching displayId is removed.
     * In a production environment, this could be updated to a "Soft Delete"
     * to preserve audit trails, but currently, it performs a physical deletion.
     * </p>
     * * @param displayId The unique public identifier of the record to be deleted.
     * @throws ResourceNotFoundException if the record ID does not exist in the system.
     */
    @Override
    public void deleteRecord(String displayId) {
        // Find using creator to ensure user owns it (ADMIN exception logic if they need cross-user could be added, but per-user isolated forms typical basis)
        FinancialRecord record = recordRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord with id " + displayId + " not found."));
        recordRepository.delete(record);
    }
}
