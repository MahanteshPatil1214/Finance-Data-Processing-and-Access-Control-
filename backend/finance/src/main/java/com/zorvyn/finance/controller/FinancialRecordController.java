package com.zorvyn.finance.controller;


import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    @Autowired
    private final FinancialRecordService recordService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        DashboardSummaryDTO summary = recordService.getSummary();
        // Return 200 OK
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    public ResponseEntity<String> createRecord(@Valid @RequestBody FinancialRecordRequestDTO request) {
        recordService.saveRecord(request);
        // Return 201 Created - Industry standard for POST
        return new ResponseEntity<>("Financial record created successfully", HttpStatus.CREATED);
    }


}
