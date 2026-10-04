package com.restaurant.erp.menu.service;

import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.menu.dto.MenuDto;
import com.restaurant.erp.menu.dto.request.CreateCategoryRequest;
import com.restaurant.erp.menu.dto.request.CreateMenuItemRequest;
import com.restaurant.erp.menu.dto.request.UpdateCategoryRequest;
import com.restaurant.erp.menu.dto.request.UpdateMenuItemRequest;
import com.restaurant.erp.menu.dto.response.CategoryResponse;
import com.restaurant.erp.menu.dto.response.MenuItemResponse;
import com.restaurant.erp.menu.entity.Category;
import com.restaurant.erp.menu.entity.MenuItem;
import com.restaurant.erp.menu.repository.CategoryRepository;
import com.restaurant.erp.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getCategoryResponses() {
        return categoryRepository.findAll().stream()
                .map(cat -> {
                    int count = menuItemRepository.findByCategoryId(cat.getId()).size();
                    return CategoryResponse.builder()
                            .id(cat.getId())
                            .name(cat.getName())
                            .displayOrder(cat.getDisplayOrder())
                            .itemCount(count)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<MenuDto.CategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName().trim())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
        Category saved = categoryRepository.save(category);
        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .displayOrder(saved.getDisplayOrder())
                .itemCount(0)
                .build();
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(Integer id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(request.getName().trim());
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        Category saved = categoryRepository.save(category);
        int count = menuItemRepository.findByCategoryId(saved.getId()).size();
        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .displayOrder(saved.getDisplayOrder())
                .itemCount(count)
                .build();
    }

    @Transactional
    public MenuDto.CategoryDto createCategory(MenuDto.CategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .build();
        return mapCategoryToDto(categoryRepository.save(category));
    }

    @Cacheable(value = "menuItems", key = "#categoryId != null ? #categoryId : 'all'")
    public List<MenuItemResponse> getMenuItemResponses(Integer categoryId) {
        List<MenuItem> items = (categoryId != null)
                ? menuItemRepository.findByCategoryId(categoryId)
                : menuItemRepository.findAll();
        return items.stream().map(this::mapToItemResponse).collect(Collectors.toList());
    }

    public List<MenuDto> getMenuItems(Integer categoryId) {
        List<MenuItem> items = (categoryId != null)
                ? menuItemRepository.findByCategoryId(categoryId)
                : menuItemRepository.findAll();
        return items.stream().map(this::mapItemToDto).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "categories"}, allEntries = true)
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        MenuItem item = MenuItem.builder()
                .category(category)
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        return mapToItemResponse(menuItemRepository.save(item));
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "categories"}, allEntries = true)
    public MenuItemResponse updateMenuItem(Integer id, UpdateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        if (request.getIsAvailable() != null) {
            item.setIsAvailable(request.getIsAvailable());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            item.setCategory(category);
        }

        return mapToItemResponse(menuItemRepository.save(item));
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

    public MenuItemResponse mapToItemResponse(MenuItem item) {
        if (item == null) return null;
        return MenuItemResponse.builder()
                .id(item.getId())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .build();
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
