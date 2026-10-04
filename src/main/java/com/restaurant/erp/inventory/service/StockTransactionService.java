package com.restaurant.erp.inventory.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.inventory.dto.IngredientDto;
import com.restaurant.erp.inventory.dto.request.CreateStockTransactionRequest;
import com.restaurant.erp.inventory.dto.response.StockTransactionResponse;
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
            return branchRepository.findAll().stream().findFirst().map(Branch::getId).orElse(1);
        }
        return branchId;
    }

    public List<StockTransactionResponse> getStockTransactionResponses() {
        return stockTransactionRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<StockTransactionResponse> getTransactionResponses() {
        return getStockTransactionResponses();
    }

    public List<IngredientDto.StockTransactionDto> getTransactions() {
        return stockTransactionRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockTransactionResponse createImport(CreateStockTransactionRequest request, String userUsername) {
        Integer branchId = getActiveBranchId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Chi nhánh không tồn tại: " + branchId));
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Nguyên liệu không tồn tại: " + request.getIngredientId()));

        User user = userRepository.findByUsername(userUsername).orElse(null);

        StockTransaction tx = StockTransaction.builder()
                .branch(branch)
                .ingredient(ingredient)
                .user(user)
                .transactionType(StockTransaction.StockTxType.IMPORT)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .note(request.getNote())
                .createdAt(ZonedDateTime.now())
                .build();

        StockTransaction saved = stockTransactionRepository.save(tx);

        // Update current stock and recalculate weighted average cost
        double oldStock = ingredient.getCurrentStock();
        double addedQty = request.getQuantity();
        double newStock = oldStock + addedQty;
        if (newStock > 0 && request.getUnitPrice() > 0) {
            double totalCost = (oldStock * ingredient.getAvgCostPrice()) + (addedQty * request.getUnitPrice());
            ingredient.setAvgCostPrice(totalCost / newStock);
        }
        ingredient.setCurrentStock(newStock);
        ingredient.setUpdatedAt(ZonedDateTime.now());
        ingredientRepository.save(ingredient);

        return mapToTransactionResponse(saved);
    }

    @Transactional
    public IngredientDto.StockTransactionDto createImport(IngredientDto.StockTransactionDto dto, String userUsername) {
        CreateStockTransactionRequest req = CreateStockTransactionRequest.builder()
                .ingredientId(dto.getIngredientId())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .note(dto.getNote())
                .transactionType(dto.getTransactionType())
                .build();
        StockTransactionResponse res = createImport(req, userUsername);
        return StockTransactionDtoAdapter(res);
    }

    private IngredientDto.StockTransactionDto StockTransactionDtoAdapter(StockTransactionResponse res) {
        return IngredientDto.StockTransactionDto.builder()
                .id(res.getId())
                .ingredientId(res.getIngredientId())
                .ingredientName(res.getIngredientName())
                .userName(res.getUserName())
                .transactionType(res.getTransactionType())
                .quantity(res.getQuantity())
                .unitPrice(res.getUnitPrice())
                .referenceOrderId(res.getReferenceOrderId())
                .note(res.getNote())
                .createdAt(res.getCreatedAt())
                .build();
    }

    public StockTransactionResponse mapToTransactionResponse(StockTransaction tx) {
        if (tx == null) return null;
        return StockTransactionResponse.builder()
                .id(tx.getId())
                .ingredientId(tx.getIngredient() != null ? tx.getIngredient().getId() : null)
                .ingredientName(tx.getIngredient() != null ? tx.getIngredient().getName() : null)
                .userName(tx.getUser() != null ? tx.getUser().getFullName() : "SYSTEM")
                .transactionType(tx.getTransactionType() != null ? tx.getTransactionType().name() : "IMPORT")
                .quantity(tx.getQuantity())
                .unitPrice(tx.getUnitPrice())
                .referenceOrderId(tx.getReferenceOrderId())
                .note(tx.getNote())
                .createdAt(tx.getCreatedAt())
                .build();
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