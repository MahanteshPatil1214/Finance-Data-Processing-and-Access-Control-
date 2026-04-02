package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.DashboardSummaryDTO;
import com.zorvyn.finance.DTOs.FinancialRecordRequestDTO;
import com.zorvyn.finance.DTOs.FinancialRecordResponseDTO;
import com.zorvyn.finance.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {


    private final FinancialRecordService recordService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        return ResponseEntity.ok(recordService.getSummary());
    }

    @GetMapping
    public ResponseEntity<Page<FinancialRecordResponseDTO>> getAllRecords(Pageable pageable) {
        return ResponseEntity.ok(recordService.getAllRecords(pageable));
    }

    @PostMapping
    public ResponseEntity<String> createRecord(@Valid @RequestBody FinancialRecordRequestDTO request) {
        recordService.saveRecord(request);
        return new ResponseEntity<>("Financial record created successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/{displayId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String displayId) {
        recordService.deleteRecord(displayId);
        return ResponseEntity.noContent().build();
    }
}
