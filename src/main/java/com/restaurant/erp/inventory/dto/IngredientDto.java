package com.restaurant.erp.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {
    private Integer id;
    private Integer branchId;
    private Integer masterId;
    private String name;
    private String unit;
    private double currentStock;
    private double minStockAlert;
    private double avgCostPrice;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientMasterDto {
        private Integer id;
        private String name;
        private String unit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipeDto {
        private Integer id;
        private Integer menuItemId;
        private Integer ingredientMasterId;
        private String ingredientMasterName;
        private double quantityRequired;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockTransactionDto {
        private Long id;
        private Integer ingredientId;
        private String ingredientName;
        private String userName;
        private String transactionType;
        private double quantity;
        private double unitPrice;
        private Long referenceOrderId;
        private String note;
        private ZonedDateTime createdAt;
    }
}
