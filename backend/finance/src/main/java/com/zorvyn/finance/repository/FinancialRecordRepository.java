package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {
    Optional<FinancialRecord> findByDisplayId(String displayId);
    List<FinancialRecord> findAllByCreator_Email(String email);
    Page<FinancialRecord> findAllByCreator_Email(String email, Pageable pageable);
    Optional<FinancialRecord> findByDisplayIdAndCreator_Email(String displayId, String email);
}
