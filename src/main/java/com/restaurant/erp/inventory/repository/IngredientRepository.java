package com.restaurant.erp.inventory.repository;

import com.restaurant.erp.inventory.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    List<Ingredient> findByBranchId(Integer branchId);
}