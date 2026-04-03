package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.DTOs.FinancialRecordResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
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

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    @Autowired
    private com.zorvyn.finance.repository.UserRepository userRepository;

    private String getCurrentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }

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

        var breakdown = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCategory().name(),
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

    @Override
    public void saveRecord(FinancialRecordRequestDTO request) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDescription(request.getDescription());

        // Attach creator
        com.zorvyn.finance.model.User creator = userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        record.setCreator(creator);

        // Defaults to current time if missing
        record.setTransactionDate(
                request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now()
        );

        recordRepository.save(record);
    }

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

    @Override
    public FinancialRecordResponseDTO getRecordById(String displayId) {
        FinancialRecord record = recordRepository.findByDisplayIdAndCreator_Email(displayId, getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord with id " + displayId + " not found or unauthorized."));
        return new FinancialRecordResponseDTO(record);
    }

    @Override
    public FinancialRecordResponseDTO updateRecord(String displayId, FinancialRecordRequestDTO request) {
        FinancialRecord record = recordRepository.findByDisplayIdAndCreator_Email(displayId, getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord with id " + displayId + " not found or unauthorized."));
        
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDescription(request.getDescription());
        if (request.getTransactionDate() != null) {
            record.setTransactionDate(request.getTransactionDate());
        }

        FinancialRecord updatedRecord = recordRepository.save(record);
        return new FinancialRecordResponseDTO(updatedRecord);
    }

    @Override
    public void deleteRecord(String displayId) {
        // Find using creator to ensure user owns it (ADMIN exception logic if they need cross-user could be added, but per-user isolated forms typical basis)
        FinancialRecord record = recordRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord with id " + displayId + " not found."));
        recordRepository.delete(record);
    }
}
