package com.restaurant.erp.table.repository;

import com.restaurant.erp.table.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<DiningTable, Integer> {
    List<DiningTable> findByBranchId(Integer branchId);
    List<DiningTable> findByBranchIdAndAreaId(Integer branchId, Integer areaId);
}
