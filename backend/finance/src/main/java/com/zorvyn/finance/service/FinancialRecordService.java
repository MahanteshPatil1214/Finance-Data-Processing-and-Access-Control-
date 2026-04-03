package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.DTOs.FinancialRecordResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FinancialRecordService {

    DashboardSummaryDTO getSummary();

    void saveRecord(FinancialRecordRequestDTO request);

    Page<FinancialRecordResponseDTO> getAllRecords(Pageable pageable);

    FinancialRecordResponseDTO getRecordById(String displayId);

    FinancialRecordResponseDTO updateRecord(String displayId, FinancialRecordRequestDTO request);

    void deleteRecord(String displayId);
}
