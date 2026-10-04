package com.restaurant.erp.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết nguyên vật liệu tại chi nhánh")
public class IngredientResponse {

    @Schema(description = "ID nguyên liệu chi nhánh", example = "1")
    private Integer id;

    @Schema(description = "ID chi nhánh", example = "1")
    private Integer branchId;

    @Schema(description = "ID nguyên liệu gốc danh mục", example = "10")
    private Integer masterId;

    @Schema(description = "Tên nguyên vật liệu", example = "Thịt bò thăn")
    private String name;

    @Schema(description = "Đơn vị tính", example = "kg")
    private String unit;

    @Schema(description = "Lượng tồn kho hiện tại", example = "45.5")
    private double currentStock;

    @Schema(description = "Mức tồn cảnh báo tối thiểu", example = "10.0")
    private double minStockAlert;

    @Schema(description = "Đơn giá vốn trung bình (VNĐ)", example = "280000")
    private double avgCostPrice;

    @Schema(description = "Thời điểm tạo")
    private ZonedDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật")
    private ZonedDateTime updatedAt;
}
