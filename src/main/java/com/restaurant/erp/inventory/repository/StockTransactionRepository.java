package com.restaurant.erp.inventory.repository;

import com.restaurant.erp.inventory.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByReferenceOrderId(Long referenceOrderId);
    List<StockTransaction> findByBranchId(Integer branchId);
}