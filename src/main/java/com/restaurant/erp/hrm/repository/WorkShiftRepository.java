package com.restaurant.erp.hrm.repository;

import com.restaurant.erp.hrm.entity.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    List<WorkShift> findByBranchId(Integer branchId);
    List<WorkShift> findByBranchIdAndShiftDate(Integer branchId, LocalDate shiftDate);
    Optional<WorkShift> findByUserIdAndShiftDateAndStatus(UUID userId, LocalDate shiftDate, WorkShift.ShiftStatus status);
}