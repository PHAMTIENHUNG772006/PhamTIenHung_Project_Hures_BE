package com.restaurant.erp.menu.service;

import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.menu.dto.MenuDto;
import com.restaurant.erp.menu.entity.Category;
import com.restaurant.erp.menu.entity.MenuItem;
import com.restaurant.erp.menu.repository.CategoryRepository;
import com.restaurant.erp.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public List<MenuDto.CategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuDto.CategoryDto createCategory(MenuDto.CategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .build();
        return mapCategoryToDto(categoryRepository.save(category));
    }

    public List<MenuDto> getMenuItems(Integer categoryId) {
        List<MenuItem> items = (categoryId != null)
                ? menuItemRepository.findByCategoryId(categoryId)
                : menuItemRepository.findAll();
        return items.stream().map(this::mapItemToDto).collect(Collectors.toList());
    }

    @Transactional
    public MenuDto createMenuItem(MenuDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        MenuItem item = MenuItem.builder()
                .category(category)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                .build();

        return mapItemToDto(menuItemRepository.save(item));
    }

    @Transactional
    public MenuDto updateMenuItem(Integer id, MenuDto dto) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImageUrl(dto.getImageUrl());
        if (dto.getIsAvailable() != null) {
            item.setIsAvailable(dto.getIsAvailable());
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            item.setCategory(category);
        }

        return mapItemToDto(menuItemRepository.save(item));
    }

    private MenuDto mapItemToDto(MenuItem item) {
        if (item == null) return null;
        return MenuDto.builder()
                .id(item.getId())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .build();
    }

    private MenuDto.CategoryDto mapCategoryToDto(Category cat) {
        if (cat == null) return null;
        return MenuDto.CategoryDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .displayOrder(cat.getDisplayOrder())
                .build();
    }
}
