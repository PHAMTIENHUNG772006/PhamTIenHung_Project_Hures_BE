package com.restaurant.erp.inventory.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.entity.Ingredient;
import com.restaurant.erp.inventory.entity.IngredientMaster;
import com.restaurant.erp.inventory.repository.IngredientMasterRepository;
import com.restaurant.erp.inventory.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMasterRepository ingredientMasterRepository;
    private final BranchRepository branchRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<IngredientDto> getIngredients() {
        return ingredientRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientDto.IngredientMasterDto createMaster(IngredientDto.IngredientMasterDto dto) {
        IngredientMaster master = IngredientMaster.builder()
                .name(dto.getName())
                .unit(dto.getUnit())
                .build();
        master = ingredientMasterRepository.save(master);
        return mapMasterToDto(master);
    }

    @Transactional
    public IngredientDto addIngredientToBranch(IngredientDto dto) {
        Branch branch = branchRepository.findById(getActiveBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        IngredientMaster master = ingredientMasterRepository.findById(dto.getMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient master not found"));

        Ingredient ing = Ingredient.builder()
                .branch(branch)
                .master(master)
                .name(master.getName())
                .unit(master.getUnit())
                .currentStock(dto.getCurrentStock())
                .minStockAlert(dto.getMinStockAlert())
                .avgCostPrice(0.0)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return mapToDto(ingredientRepository.save(ing));
    }

    private IngredientDto mapToDto(Ingredient ing) {
        return IngredientDto.builder()
                .id(ing.getId())
                .branchId(ing.getBranch().getId())
                .masterId(ing.getMaster().getId())
                .name(ing.getName())
                .unit(ing.getUnit())
                .currentStock(ing.getCurrentStock())
                .minStockAlert(ing.getMinStockAlert())
                .avgCostPrice(ing.getAvgCostPrice())
                .createdAt(ing.getCreatedAt())
                .updatedAt(ing.getUpdatedAt())
                .build();
    }

    private IngredientDto.IngredientMasterDto mapMasterToDto(IngredientMaster master) {
        return IngredientDto.IngredientMasterDto.builder()
                .id(master.getId())
                .name(master.getName())
                .unit(master.getUnit())
                .build();
    }
}