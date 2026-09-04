package com.restaurant.erp.inventory.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.service.IngredientService;
import com.restaurant.erp.inventory.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientDto>>> getIngredients() {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.getIngredients()));
    }

    @PostMapping("/master")
    public ResponseEntity<ApiResponse<IngredientDto.IngredientMasterDto>> createMaster(
            @Valid @RequestBody IngredientDto.IngredientMasterDto dto) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.createMaster(dto), "Master ingredient registered"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IngredientDto>> addIngredientToBranch(@Valid @RequestBody IngredientDto dto) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.addIngredientToBranch(dto), "Linked master ingredient to branch"));
    }

    @GetMapping("/recipes/{menuItemId}")
    public ResponseEntity<ApiResponse<List<IngredientDto.RecipeDto>>> getRecipesByMenuItem(@PathVariable Integer menuItemId) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.getRecipesByMenuItem(menuItemId)));
    }

    @PostMapping("/recipes")
    public ResponseEntity<ApiResponse<IngredientDto.RecipeDto>> addRecipeLine(@Valid @RequestBody IngredientDto.RecipeDto dto) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.addRecipeLine(dto), "Recipe instruction added"));
    }
}