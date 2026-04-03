package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID>, JpaSpecificationExecutor<FinancialRecord> {
    Optional<FinancialRecord> findByDisplayId(String displayId);
    List<FinancialRecord> findAllByCreator_Email(String email);
    Page<FinancialRecord> findAllByCreator_Email(String email, Pageable pageable);
    Optional<FinancialRecord> findByDisplayIdAndCreator_Email(String displayId, String email);
}
