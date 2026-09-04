package com.restaurant.erp.financial.repository;

import com.restaurant.erp.financial.entity.DailyFinancialSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyFinancialSummaryRepository extends JpaRepository<DailyFinancialSummary, Long> {
    List<DailyFinancialSummary> findByBranchId(Integer branchId);
}