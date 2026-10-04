package com.restaurant.erp.inventory.service;

import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.dto.request.CreateRecipeRequest;
import com.restaurant.erp.inventory.dto.response.RecipeResponse;
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

    public List<RecipeResponse> getRecipeResponsesByMenuItem(Integer menuItemId) {
        return recipeRepository.findByMenuItemId(menuItemId).stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());
    }

    public List<IngredientDto.RecipeDto> getRecipesByMenuItem(Integer menuItemId) {
        return recipeRepository.findByMenuItemId(menuItemId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipeResponse addRecipeLine(CreateRecipeRequest request) {
        MenuItem item = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn không tồn tại: " + request.getMenuItemId()));
        IngredientMaster master = ingredientMasterRepository.findById(request.getIngredientMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Nguyên liệu gốc không tồn tại: " + request.getIngredientMasterId()));

        Recipe recipe = Recipe.builder()
                .menuItem(item)
                .ingredientMaster(master)
                .quantityRequired(request.getQuantityRequired())
                .build();

        return mapToRecipeResponse(recipeRepository.save(recipe));
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

    public RecipeResponse mapToRecipeResponse(Recipe r) {
        if (r == null) return null;
        return RecipeResponse.builder()
                .id(r.getId())
                .menuItemId(r.getMenuItem() != null ? r.getMenuItem().getId() : null)
                .ingredientMasterId(r.getIngredientMaster() != null ? r.getIngredientMaster().getId() : null)
                .ingredientMasterName(r.getIngredientMaster() != null ? r.getIngredientMaster().getName() : null)
                .quantityRequired(r.getQuantityRequired())
                .build();
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