package com.restaurant.erp.financial.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "vw_daily_financial_summary")
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyFinancialSummary {

    @Id
    private Long id;
    private Integer branchId;
    private LocalDate reportDate;
    private Integer totalOrders;
    private Double grossRevenue;
    private Double totalFoodCost;
    private Double foodCostPercentage;
}