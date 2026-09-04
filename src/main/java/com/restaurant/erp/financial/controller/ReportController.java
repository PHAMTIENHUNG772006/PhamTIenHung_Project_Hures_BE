package com.restaurant.erp.financial.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.financial.dto.FinancialReportDto;
import com.restaurant.erp.financial.service.FinancialReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/financial/reports")
@RequiredArgsConstructor
public class ReportController {

    private final FinancialReportService financialReportService;

    @GetMapping("/daily-summary")
    public ResponseEntity<ApiResponse<List<FinancialReportDto>>> getDailyFinancialSummary() {
        return ResponseEntity.ok(ApiResponse.success(financialReportService.getDailyFinancialSummary()));
    }
}