package com.restaurant.erp.inventory.repository;

import com.restaurant.erp.inventory.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByMenuItemId(Integer menuItemId);
}