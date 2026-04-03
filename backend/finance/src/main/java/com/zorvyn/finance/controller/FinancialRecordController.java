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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {


    private final FinancialRecordService recordService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        return ResponseEntity.ok(recordService.getSummary());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @GetMapping
    public ResponseEntity<Page<FinancialRecordResponseDTO>> getAllRecords(Pageable pageable) {
        return ResponseEntity.ok(recordService.getAllRecords(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createRecord(@Valid @RequestBody FinancialRecordRequestDTO request) {
        recordService.saveRecord(request);
        return new ResponseEntity<>("Financial record created successfully", HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @GetMapping("/{displayId}")
    public ResponseEntity<FinancialRecordResponseDTO> getRecordById(@PathVariable String displayId) {
        return ResponseEntity.ok(recordService.getRecordById(displayId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{displayId}")
    public ResponseEntity<FinancialRecordResponseDTO> updateRecord(
            @PathVariable String displayId,
            @Valid @RequestBody FinancialRecordRequestDTO request) {
        return ResponseEntity.ok(recordService.updateRecord(displayId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{displayId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String displayId) {
        recordService.deleteRecord(displayId);
        return ResponseEntity.noContent().build();
    }
}
