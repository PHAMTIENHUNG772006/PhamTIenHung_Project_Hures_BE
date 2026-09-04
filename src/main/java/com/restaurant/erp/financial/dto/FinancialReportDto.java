package com.restaurant.erp.financial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReportDto {
    private Integer branchId;
    private LocalDate reportDate;
    private Integer totalOrders;
    private Double grossRevenue;
    private Double totalFoodCost;
    private Double foodCostPercentage;
}
