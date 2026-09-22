package com.restaurant.erp.inventory.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.inventory.dto.DailyAllocationDto;
import com.restaurant.erp.inventory.entity.DailyIngredientAllocation;
import com.restaurant.erp.inventory.entity.Ingredient;
import com.restaurant.erp.inventory.entity.StockTransaction;
import com.restaurant.erp.inventory.repository.DailyIngredientAllocationRepository;
import com.restaurant.erp.inventory.repository.IngredientRepository;
import com.restaurant.erp.inventory.repository.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyAllocationService {

    private final DailyIngredientAllocationRepository allocationRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchRepository branchRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Transactional(readOnly = true)
    public List<DailyAllocationDto.Response> getAllocations(Integer branchId, LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<DailyIngredientAllocation> list = allocationRepository.findByBranchIdAndAllocationDate(branchId, targetDate);
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<DailyAllocationDto.Response> createBatchAllocation(DailyAllocationDto.CreateBatchRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with ID: " + request.getBranchId()));

        LocalDate allocDate = (request.getAllocationDate() != null) ? request.getAllocationDate() : LocalDate.now();
        List<DailyIngredientAllocation> results = new ArrayList<>();

        for (DailyAllocationDto.CreateItemRequest item : request.getItems()) {
            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with ID: " + item.getIngredientId()));

            DailyIngredientAllocation allocation = allocationRepository
                    .findByBranchIdAndIngredientIdAndAllocationDate(branch.getId(), ingredient.getId(), allocDate)
                    .orElse(DailyIngredientAllocation.builder()
                            .branch(branch)
                            .ingredient(ingredient)
                            .allocationDate(allocDate)
                            .allocatedQuantity(0.0)
                            .usedQuantity(0.0)
                            .returnedQuantity(0.0)
                            .lossQuantity(0.0)
                            .status("OPEN")
                            .createdAt(ZonedDateTime.now())
                            .build());

            double newAllocated = allocation.getAllocatedQuantity() + item.getAllocatedQuantity();
            allocation.setAllocatedQuantity(newAllocated);
            allocation.setSource(item.getSource() != null ? item.getSource() : "LOCAL_STOCK");
            allocation.setCentralBatchNo(item.getCentralBatchNo());
            allocation.setNote(item.getNote());
            allocation.setUpdatedAt(ZonedDateTime.now());

            // Deduct from warehouse stock if from LOCAL_STOCK
            if (!"CENTRAL_KITCHEN".equalsIgnoreCase(item.getSource())) {
                double updatedStock = Math.max(0, ingredient.getCurrentStock() - item.getAllocatedQuantity());
                ingredient.setCurrentStock(updatedStock);
                ingredient.setUpdatedAt(ZonedDateTime.now());
                ingredientRepository.save(ingredient);

                // Record DAILY_ISSUE transaction
                StockTransaction txn = StockTransaction.builder()
                        .branch(branch)
                        .ingredient(ingredient)
                        .transactionType(StockTransaction.StockTxType.DAILY_ISSUE)
                        .quantity(item.getAllocatedQuantity())
                        .unitPrice(ingredient.getAvgCostPrice())
                        .note("Xuất cấp đầu ngày cho bếp: " + (item.getNote() != null ? item.getNote() : ""))
                        .createdAt(ZonedDateTime.now())
                        .build();
                stockTransactionRepository.save(txn);
            } else {
                // Record CENTRAL_KITCHEN_TRANSFER transaction
                StockTransaction txn = StockTransaction.builder()
                        .branch(branch)
                        .ingredient(ingredient)
                        .transactionType(StockTransaction.StockTxType.CENTRAL_KITCHEN_TRANSFER)
                        .quantity(item.getAllocatedQuantity())
                        .unitPrice(ingredient.getAvgCostPrice())
                        .note("Tiếp nhận từ Bếp Tổng - Lô: " + item.getCentralBatchNo())
                        .createdAt(ZonedDateTime.now())
                        .build();
                stockTransactionRepository.save(txn);
            }

            results.add(allocationRepository.save(allocation));
        }

        return results.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<DailyAllocationDto.Response> reconcileBatch(DailyAllocationDto.ReconcileBatchRequest request) {
        List<DailyIngredientAllocation> updatedList = new ArrayList<>();

        for (DailyAllocationDto.ReconcileItemRequest item : request.getItems()) {
            DailyIngredientAllocation alloc = allocationRepository.findById(item.getAllocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Allocation not found with ID: " + item.getAllocationId()));

            double expected = Math.max(0, alloc.getAllocatedQuantity() - alloc.getUsedQuantity());
            double actual = (item.getActualRemaining() != null) ? item.getActualRemaining() : expected;
            double loss = Math.max(0, expected - actual);
            double returned = actual;

            alloc.setActualRemaining(actual);
            alloc.setReturnedQuantity(returned);
            alloc.setLossQuantity(loss);
            alloc.setLossReason(item.getLossReason());
            alloc.setStatus("RECONCILED");
            alloc.setUpdatedAt(ZonedDateTime.now());

            Ingredient ing = alloc.getIngredient();
            Branch branch = alloc.getBranch();

            // Return remaining back to warehouse stock
            if (returned > 0) {
                ing.setCurrentStock(ing.getCurrentStock() + returned);
                ing.setUpdatedAt(ZonedDateTime.now());
                ingredientRepository.save(ing);

                StockTransaction returnTxn = StockTransaction.builder()
                        .branch(branch)
                        .ingredient(ing)
                        .transactionType(StockTransaction.StockTxType.DAILY_RETURN)
                        .quantity(returned)
                        .unitPrice(ing.getAvgCostPrice())
                        .note("Hoàn trả tồn dư cuối ngày từ bếp về kho tổng")
                        .createdAt(ZonedDateTime.now())
                        .build();
                stockTransactionRepository.save(returnTxn);
            }

            // Record loss / waste discrepancy if any
            if (loss > 0) {
                StockTransaction lossTxn = StockTransaction.builder()
                        .branch(branch)
                        .ingredient(ing)
                        .transactionType(StockTransaction.StockTxType.WASTE_DISCREPANCY)
                        .quantity(loss)
                        .unitPrice(ing.getAvgCostPrice())
                        .note("Hao hụt kiểm kê cuối ca: " + (item.getLossReason() != null ? item.getLossReason() : ""))
                        .createdAt(ZonedDateTime.now())
                        .build();
                stockTransactionRepository.save(lossTxn);
            }

            updatedList.add(allocationRepository.save(alloc));
        }

        return updatedList.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void recordConsumption(Integer branchId, Integer ingredientId, LocalDate date, Double quantityUsed) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        allocationRepository.findByBranchIdAndIngredientIdAndAllocationDate(branchId, ingredientId, targetDate)
                .ifPresent(alloc -> {
                    alloc.setUsedQuantity(alloc.getUsedQuantity() + quantityUsed);
                    alloc.setUpdatedAt(ZonedDateTime.now());
                    allocationRepository.save(alloc);
                });
    }

    private DailyAllocationDto.Response mapToResponse(DailyIngredientAllocation a) {
        Ingredient ing = a.getIngredient();
        Branch b = a.getBranch();
        double cost = (ing != null) ? ing.getAvgCostPrice() : 0.0;
        double lossCost = (a.getLossQuantity() != null) ? a.getLossQuantity() * cost : 0.0;

        return DailyAllocationDto.Response.builder()
                .id(a.getId())
                .branchId(b != null ? b.getId() : null)
                .branchName(b != null ? b.getName() : null)
                .ingredientId(ing != null ? ing.getId() : null)
                .ingredientName(ing != null ? ing.getName() : "Unknown")
                .sku(ing != null ? "ING-" + ing.getId() : null)
                .unit(ing != null ? ing.getUnit() : "kg")
                .costPerUnit(cost)
                .allocationDate(a.getAllocationDate())
                .allocatedQuantity(a.getAllocatedQuantity())
                .usedQuantity(a.getUsedQuantity())
                .actualRemaining(a.getActualRemaining())
                .returnedQuantity(a.getReturnedQuantity())
                .lossQuantity(a.getLossQuantity())
                .lossReason(a.getLossReason())
                .lossCost(lossCost)
                .source(a.getSource())
                .centralBatchNo(a.getCentralBatchNo())
                .status(a.getStatus())
                .note(a.getNote())
                .build();
    }
}