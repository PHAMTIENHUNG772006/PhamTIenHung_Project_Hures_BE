package com.restaurant.erp.inventory.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.inventory.dto.request.CreateIngredientMasterRequest;
import com.restaurant.erp.inventory.dto.request.CreateIngredientRequest;
import com.restaurant.erp.inventory.dto.request.CreateRecipeRequest;
import com.restaurant.erp.inventory.dto.response.IngredientMasterResponse;
import com.restaurant.erp.inventory.dto.response.IngredientResponse;
import com.restaurant.erp.inventory.dto.response.RecipeResponse;
import com.restaurant.erp.inventory.service.IngredientService;
import com.restaurant.erp.inventory.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/ingredients")
@RequiredArgsConstructor
@Tag(name = "Inventory & Ingredients", description = "APIs for warehouse ingredients and recipe formulas")
public class IngredientController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "Lấy danh sách các nguyên liệu kho tại chi nhánh")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getIngredients() {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.getIngredientResponses()));
    }

    @PostMapping("/master")
    @Operation(summary = "Đăng ký nguyên liệu mới vào danh mục tổng")
    public ResponseEntity<ApiResponse<IngredientMasterResponse>> createMaster(
            @Valid @RequestBody CreateIngredientMasterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.createMaster(request), "Đăng ký nguyên liệu master thành công"));
    }

    @PostMapping
    @Operation(summary = "Thêm nguyên liệu vào kho chi nhánh")
    public ResponseEntity<ApiResponse<IngredientResponse>> addIngredientToBranch(@Valid @RequestBody CreateIngredientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ingredientService.addIngredientToBranch(request), "Thêm nguyên liệu kho thành công"));
    }

    @GetMapping("/recipes/{menuItemId}")
    @Operation(summary = "Lấy danh sách định lượng công thức theo món ăn")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getRecipesByMenuItem(@PathVariable Integer menuItemId) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.getRecipeResponsesByMenuItem(menuItemId)));
    }

    @PostMapping("/recipes")
    @Operation(summary = "Thêm dòng định lượng công thức cho món ăn")
    public ResponseEntity<ApiResponse<RecipeResponse>> addRecipeLine(@Valid @RequestBody CreateRecipeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(recipeService.addRecipeLine(request), "Thêm dòng công thức thành công"));
    }
}