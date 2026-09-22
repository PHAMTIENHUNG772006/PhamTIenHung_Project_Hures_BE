package com.restaurant.erp.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DailyAllocationDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Integer branchId;
        private String branchName;
        private Integer ingredientId;
        private String ingredientName;
        private String sku;
        private String unit;
        private Double costPerUnit;
        private LocalDate allocationDate;
        private Double allocatedQuantity;
        private Double usedQuantity;
        private Double actualRemaining;
        private Double returnedQuantity;
        private Double lossQuantity;
        private String lossReason;
        private Double lossCost;
        private String source;
        private String centralBatchNo;
        private String status;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateItemRequest {
        @NotNull(message = "Mã nguyên liệu không được để trống")
        private Integer ingredientId;

        @NotNull(message = "Số lượng cấp phát không được để trống")
        @Positive(message = "Số lượng cấp phát bếp phải lớn hơn 0")
        private Double allocatedQuantity;

        private String source;
        private String centralBatchNo;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateBatchRequest {
        @NotNull(message = "Chi nhánh không được để trống")
        private Integer branchId;

        @NotNull(message = "Ngày cấp phát không được để trống")
        private LocalDate allocationDate;

        @NotEmpty(message = "Danh sách nguyên liệu cấp phát không được rỗng")
        @Valid
        private List<CreateItemRequest> items;

        private String creatorName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReconcileItemRequest {
        @NotNull(message = "Mã cấp phát không được để trống")
        private Long allocationId;

        @NotNull(message = "Lượng thực tế còn lại không được để trống")
        @PositiveOrZero(message = "Lượng thực tế còn lại không được âm")
        private Double actualRemaining;

        private String lossReason;
        private Boolean returnToStock;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReconcileBatchRequest {
        @NotNull(message = "Chi nhánh không được để trống")
        private Integer branchId;

        @NotNull(message = "Ngày chốt kiểm kê không được để trống")
        private LocalDate allocationDate;

        private String reconciledBy;

        @NotEmpty(message = "Danh sách kiểm kê không được rỗng")
        @Valid
        private List<ReconcileItemRequest> items;

        private String generalNote;
    }
}
