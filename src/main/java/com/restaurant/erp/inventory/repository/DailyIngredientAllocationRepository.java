package com.restaurant.erp.inventory.repository;

import com.restaurant.erp.inventory.entity.DailyIngredientAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyIngredientAllocationRepository extends JpaRepository<DailyIngredientAllocation, Long> {

    List<DailyIngredientAllocation> findByBranchIdAndAllocationDate(Integer branchId, LocalDate allocationDate);

    Optional<DailyIngredientAllocation> findByBranchIdAndIngredientIdAndAllocationDate(
            Integer branchId,
            Integer ingredientId,
            LocalDate allocationDate
    );

    @Query("SELECT d FROM DailyIngredientAllocation d WHERE d.branch.id = :branchId AND d.allocationDate = :date AND d.status = 'OPEN'")
    List<DailyIngredientAllocation> findOpenAllocations(
            @Param("branchId") Integer branchId,
            @Param("date") LocalDate date
    );
}