package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu thêm dòng định lượng nguyên liệu (Recipe Line) cho món ăn")
public class CreateRecipeRequest {

    @Schema(description = "ID món ăn trong thực đơn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã món ăn không được để trống")
    private Integer menuItemId;

    @Schema(description = "ID nguyên vật liệu danh mục tổng", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã nguyên liệu không được để trống")
    private Integer ingredientMasterId;

    @Schema(description = "Định lượng tiêu hao cần cho 1 phần món ăn", example = "0.25", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "Định lượng nguyên liệu phải lớn hơn 0")
    private double quantityRequired;
}
