package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {
    Optional<FinancialRecord> findByDisplayId(String displayId);
}
