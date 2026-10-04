package com.restaurant.erp.menu.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.menu.dto.request.CreateCategoryRequest;
import com.restaurant.erp.menu.dto.request.CreateMenuItemRequest;
import com.restaurant.erp.menu.dto.request.UpdateMenuItemRequest;
import com.restaurant.erp.menu.dto.response.CategoryResponse;
import com.restaurant.erp.menu.dto.response.MenuItemResponse;
import com.restaurant.erp.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "APIs for restaurant menu and categories")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/api/menu/categories")
    @Operation(summary = "Lấy danh sách các danh mục món ăn")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getCategoryResponses()));
    }

    @PostMapping("/api/menu/categories")
    @Operation(summary = "Tạo mới danh mục món ăn")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.createCategory(request), "Tạo danh mục thành công"));
    }

    @GetMapping("/api/menu/items")
    @Operation(summary = "Lấy danh sách các món ăn (có thể lọc theo danh mục)")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getMenuItems(@RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuItemResponses(categoryId)));
    }

    @PostMapping("/api/menu/items")
    @Operation(summary = "Thêm món ăn mới vào thực đơn")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.createMenuItem(request), "Thêm món ăn thành công"));
    }

    @PutMapping("/api/menu/items/{id}")
    @Operation(summary = "Cập nhật thông tin món ăn")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItem(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.updateMenuItem(id, request), "Cập nhật món ăn thành công"));
    }
}
