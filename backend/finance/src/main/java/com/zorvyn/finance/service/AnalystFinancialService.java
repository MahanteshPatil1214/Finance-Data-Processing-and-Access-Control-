package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.HighSpenderDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AnalystFinancialService {

    Map<String, Object> getGlobalPlatformStats();

    List<HighSpenderDTO> getHighSpenderAlerts(BigDecimal threshold);
}
