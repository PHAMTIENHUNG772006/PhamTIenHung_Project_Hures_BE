package com.restaurant.erp.inventory.repository;

import com.restaurant.erp.inventory.entity.IngredientMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientMasterRepository extends JpaRepository<IngredientMaster, Integer> {
}