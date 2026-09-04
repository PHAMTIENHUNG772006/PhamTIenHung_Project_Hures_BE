package com.restaurant.erp.inventory.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.entity.Ingredient;
import com.restaurant.erp.inventory.entity.StockTransaction;
import com.restaurant.erp.inventory.repository.IngredientRepository;
import com.restaurant.erp.inventory.repository.StockTransactionRepository;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<IngredientDto.StockTransactionDto> getTransactions() {
        return stockTransactionRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientDto.StockTransactionDto createImport(IngredientDto.StockTransactionDto dto, String userEmail) {
        Integer branchId = getActiveBranchId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found"));
        
        User user = userRepository.findByEmail(userEmail).orElse(null);

        StockTransaction tx = StockTransaction.builder()
                .branch(branch)
                .ingredient(ingredient)
                .user(user)
                .transactionType(StockTransaction.StockTxType.IMPORT)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .note(dto.getNote())
                .createdAt(ZonedDateTime.now())
                .build();

        tx = stockTransactionRepository.save(tx);
        return mapToDto(tx);
    }

    private IngredientDto.StockTransactionDto mapToDto(StockTransaction tx) {
        return IngredientDto.StockTransactionDto.builder()
                .id(tx.getId())
                .ingredientId(tx.getIngredient().getId())
                .ingredientName(tx.getIngredient().getName())
                .userName(tx.getUser() != null ? tx.getUser().getFullName() : "SYSTEM")
                .transactionType(tx.getTransactionType().name())
                .quantity(tx.getQuantity())
                .unitPrice(tx.getUnitPrice())
                .referenceOrderId(tx.getReferenceOrderId())
                .note(tx.getNote())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}