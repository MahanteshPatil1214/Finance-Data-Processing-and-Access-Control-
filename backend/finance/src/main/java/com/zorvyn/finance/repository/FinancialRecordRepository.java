package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord,Long> {

}
