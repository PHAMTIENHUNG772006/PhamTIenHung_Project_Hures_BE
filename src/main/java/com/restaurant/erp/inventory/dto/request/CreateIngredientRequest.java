package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu liên kết hoặc tạo nguyên liệu kho tại chi nhánh")
public class CreateIngredientRequest {

    @Schema(description = "ID chi nhánh", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã chi nhánh không được để trống")
    private Integer branchId;

    @Schema(description = "ID nguyên liệu master (nếu liên kết)", example = "10")
    private Integer masterId;

    @Schema(description = "Tên nguyên vật liệu", example = "Thịt bò thăn", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên nguyên vật liệu không được để trống")
    private String name;

    @Schema(description = "Đơn vị tính", example = "kg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Đơn vị tính không được để trống")
    private String unit;

    @Schema(description = "Số lượng tồn kho ban đầu", example = "50")
    @PositiveOrZero(message = "Số lượng tồn kho không được âm")
    @Builder.Default
    private double currentStock = 0.0;

    @Schema(description = "Mức tồn tối thiểu cảnh báo", example = "10")
    @PositiveOrZero(message = "Mức tồn tối thiểu cảnh báo không được âm")
    @Builder.Default
    private double minStockAlert = 5.0;

    @Schema(description = "Đơn giá vốn trung bình (VNĐ)", example = "280000")
    @PositiveOrZero(message = "Đơn giá vốn trung bình không được âm")
    @Builder.Default
    private double avgCostPrice = 0.0;
}
