package com.restaurant.erp.menu.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.menu.dto.MenuDto;
import com.restaurant.erp.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/api/menu/categories")
    public ResponseEntity<ApiResponse<List<MenuDto.CategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getCategories()));
    }

    @PostMapping("/api/menu/categories")
    public ResponseEntity<ApiResponse<MenuDto.CategoryDto>> createCategory(@Valid @RequestBody MenuDto.CategoryDto dto) {
        return ResponseEntity.ok(ApiResponse.success(menuService.createCategory(dto), "Category created successfully"));
    }

    @GetMapping("/api/menu/items")
    public ResponseEntity<ApiResponse<List<MenuDto>>> getMenuItems(@RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuItems(categoryId)));
    }

    @PostMapping("/api/menu/items")
    public ResponseEntity<ApiResponse<MenuDto>> createMenuItem(@Valid @RequestBody MenuDto dto) {
        return ResponseEntity.ok(ApiResponse.success(menuService.createMenuItem(dto), "Menu item created successfully"));
    }

    @PutMapping("/api/menu/items/{id}")
    public ResponseEntity<ApiResponse<MenuDto>> updateMenuItem(
            @PathVariable Integer id,
            @Valid @RequestBody MenuDto dto) {
        return ResponseEntity.ok(ApiResponse.success(menuService.updateMenuItem(id, dto), "Menu item updated successfully"));
    }
}
