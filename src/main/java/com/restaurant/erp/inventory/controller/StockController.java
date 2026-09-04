package com.restaurant.erp.inventory.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.service.StockTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockTransactionService stockTransactionService;

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<IngredientDto.StockTransactionDto>>> getTransactions() {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.getTransactions()));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<IngredientDto.StockTransactionDto>> createImport(
            @Valid @RequestBody IngredientDto.StockTransactionDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : "system@restaurant.com";
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.createImport(dto, email), "Import transaction recorded successfully"));
    }
}