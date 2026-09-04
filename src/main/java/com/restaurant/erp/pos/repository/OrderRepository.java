package com.restaurant.erp.pos.repository;

import com.restaurant.erp.pos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBranchId(Integer branchId);
}
