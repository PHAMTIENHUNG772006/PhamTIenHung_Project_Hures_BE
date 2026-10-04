package com.restaurant.erp.inventory.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.dto.request.CreateIngredientMasterRequest;
import com.restaurant.erp.inventory.dto.request.CreateIngredientRequest;
import com.restaurant.erp.inventory.dto.response.IngredientMasterResponse;
import com.restaurant.erp.inventory.dto.response.IngredientResponse;
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
            return branchRepository.findAll().stream().findFirst().map(Branch::getId).orElse(1);
        }
        return branchId;
    }

    public List<IngredientResponse> getIngredientResponses() {
        return ingredientRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToIngredientResponse)
                .collect(Collectors.toList());
    }

    public List<IngredientDto> getIngredients() {
        return ingredientRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientMasterResponse createMaster(CreateIngredientMasterRequest request) {
        IngredientMaster master = IngredientMaster.builder()
                .name(request.getName().trim())
                .unit(request.getUnit().trim())
                .build();
        master = ingredientMasterRepository.save(master);
        return IngredientMasterResponse.builder()
                .id(master.getId())
                .name(master.getName())
                .unit(master.getUnit())
                .build();
    }

    @Transactional
    public IngredientResponse addIngredientToBranch(CreateIngredientRequest request) {
        Integer branchId = request.getBranchId() != null ? request.getBranchId() : getActiveBranchId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Chi nhánh không tồn tại: " + branchId));

        IngredientMaster master = null;
        if (request.getMasterId() != null) {
            master = ingredientMasterRepository.findById(request.getMasterId()).orElse(null);
        }
        if (master == null) {
            master = ingredientMasterRepository.save(
                    IngredientMaster.builder()
                            .name(request.getName().trim())
                            .unit(request.getUnit().trim())
                            .build()
            );
        }

        Ingredient ing = Ingredient.builder()
                .branch(branch)
                .master(master)
                .name(request.getName().trim())
                .unit(request.getUnit().trim())
                .currentStock(request.getCurrentStock())
                .minStockAlert(request.getMinStockAlert())
                .avgCostPrice(request.getAvgCostPrice())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return mapToIngredientResponse(ingredientRepository.save(ing));
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

    public IngredientResponse mapToIngredientResponse(Ingredient ing) {
        if (ing == null) return null;
        return IngredientResponse.builder()
                .id(ing.getId())
                .branchId(ing.getBranch() != null ? ing.getBranch().getId() : null)
                .masterId(ing.getMaster() != null ? ing.getMaster().getId() : null)
                .name(ing.getName())
                .unit(ing.getUnit())
                .currentStock(ing.getCurrentStock())
                .minStockAlert(ing.getMinStockAlert())
                .avgCostPrice(ing.getAvgCostPrice())
                .createdAt(ing.getCreatedAt())
                .updatedAt(ing.getUpdatedAt())
                .build();
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