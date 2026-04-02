package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import jakarta.validation.Valid;

public interface FinancialRecordService {

    DashboardSummaryDTO getSummary();

    void saveRecord(@Valid FinancialRecordRequestDTO request);
}
