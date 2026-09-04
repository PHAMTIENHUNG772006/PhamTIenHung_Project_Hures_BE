package com.restaurant.erp.reservation.repository;

import com.restaurant.erp.reservation.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBranchId(Integer branchId);
}
