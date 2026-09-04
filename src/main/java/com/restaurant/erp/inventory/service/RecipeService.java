package com.restaurant.erp.inventory.service;

import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.entity.IngredientMaster;
import com.restaurant.erp.inventory.entity.Recipe;
import com.restaurant.erp.inventory.repository.IngredientMasterRepository;
import com.restaurant.erp.inventory.repository.RecipeRepository;
import com.restaurant.erp.menu.entity.MenuItem;
import com.restaurant.erp.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientMasterRepository ingredientMasterRepository;

    public List<IngredientDto.RecipeDto> getRecipesByMenuItem(Integer menuItemId) {
        return recipeRepository.findByMenuItemId(menuItemId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientDto.RecipeDto addRecipeLine(IngredientDto.RecipeDto dto) {
        MenuItem item = menuItemRepository.findById(dto.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));
        IngredientMaster master = ingredientMasterRepository.findById(dto.getIngredientMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("IngredientMaster not found"));

        Recipe recipe = Recipe.builder()
                .menuItem(item)
                .ingredientMaster(master)
                .quantityRequired(dto.getQuantityRequired())
                .build();

        return mapToDto(recipeRepository.save(recipe));
    }

    private IngredientDto.RecipeDto mapToDto(Recipe r) {
        return IngredientDto.RecipeDto.builder()
                .id(r.getId())
                .menuItemId(r.getMenuItem().getId())
                .ingredientMasterId(r.getIngredientMaster().getId())
                .ingredientMasterName(r.getIngredientMaster().getName())
                .quantityRequired(r.getQuantityRequired())
                .build();
    }
}