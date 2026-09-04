package com.restaurant.erp.financial.service;

import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.financial.dto.FinancialReportDto;
import com.restaurant.erp.financial.entity.DailyFinancialSummary;
import com.restaurant.erp.financial.repository.DailyFinancialSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialReportService {

    private final DailyFinancialSummaryRepository dailyFinancialSummaryRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<FinancialReportDto> getDailyFinancialSummary() {
        return dailyFinancialSummaryRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FinancialReportDto mapToDto(DailyFinancialSummary sum) {
        return FinancialReportDto.builder()
                .branchId(sum.getBranchId())
                .reportDate(sum.getReportDate())
                .totalOrders(sum.getTotalOrders())
                .grossRevenue(sum.getGrossRevenue())
                .totalFoodCost(sum.getTotalFoodCost())
                .foodCostPercentage(sum.getFoodCostPercentage())
                .build();
    }
}