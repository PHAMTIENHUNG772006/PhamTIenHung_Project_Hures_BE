package com.restaurant.erp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull(message = "Mã chi nhánh không được để trống")
    private Integer branchId;

    private Integer masterId;

    @NotBlank(message = "Tên nguyên vật liệu không được để trống")
    private String name;

    @NotBlank(message = "Đơn vị tính không được để trống")
    private String unit;

    @PositiveOrZero(message = "Số lượng tồn kho không được âm")
    private double currentStock;

    @PositiveOrZero(message = "Mức tồn tối thiểu cảnh báo không được âm")
    private double minStockAlert;

    @PositiveOrZero(message = "Đơn giá vốn trung bình không được âm")
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

        @NotBlank(message = "Tên danh mục nguyên liệu không được để trống")
        private String name;

        @NotBlank(message = "Đơn vị tính không được để trống")
        private String unit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipeDto {
        private Integer id;

        @NotNull(message = "Mã món ăn không được để trống")
        private Integer menuItemId;

        @NotNull(message = "Mã nguyên liệu không được để trống")
        private Integer ingredientMasterId;

        private String ingredientMasterName;

        @Positive(message = "Định lượng nguyên liệu phải lớn hơn 0")
        private double quantityRequired;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockTransactionDto {
        private Long id;

        @NotNull(message = "Nguyên vật liệu không được để trống")
        private Integer ingredientId;

        private String ingredientName;
        private String userName;
        private String transactionType;

        @Positive(message = "Số lượng phải lớn hơn 0")
        private double quantity;

        @PositiveOrZero(message = "Đơn giá không được âm")
        private double unitPrice;

        private Long referenceOrderId;
        private String note;
        private ZonedDateTime createdAt;
    }
}
