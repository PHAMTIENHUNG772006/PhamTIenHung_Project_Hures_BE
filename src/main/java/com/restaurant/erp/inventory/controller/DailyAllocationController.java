package com.restaurant.erp.inventory.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.inventory.dto.DailyAllocationDto;
import com.restaurant.erp.inventory.service.DailyAllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory/allocations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DailyAllocationController {

    private final DailyAllocationService dailyAllocationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DailyAllocationDto.Response>>> getAllocations(
            @RequestParam Integer branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(
                dailyAllocationService.getAllocations(branchId, date),
                "Daily kitchen allocations retrieved"
        ));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<DailyAllocationDto.Response>>> createBatch(
            @Valid @RequestBody DailyAllocationDto.CreateBatchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                dailyAllocationService.createBatchAllocation(request),
                "Daily kitchen allocations created successfully"
        ));
    }

    @PostMapping("/reconcile")
    public ResponseEntity<ApiResponse<List<DailyAllocationDto.Response>>> reconcileBatch(
            @Valid @RequestBody DailyAllocationDto.ReconcileBatchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                dailyAllocationService.reconcileBatch(request),
                "Daily kitchen reconciliation completed and returned to warehouse"
        ));
    }

    @PostMapping("/consumption")
    public ResponseEntity<ApiResponse<String>> recordConsumption(
            @RequestParam Integer branchId,
            @RequestParam Integer ingredientId,
            @RequestParam Double quantity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        dailyAllocationService.recordConsumption(branchId, ingredientId, date, quantity);
        return ResponseEntity.ok(ApiResponse.success("OK", "Consumption recorded"));
    }
}
