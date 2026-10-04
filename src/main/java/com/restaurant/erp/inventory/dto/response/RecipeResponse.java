package com.restaurant.erp.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin dòng định lượng nguyên liệu của món ăn")
public class RecipeResponse {

    @Schema(description = "ID dòng định lượng", example = "1")
    private Integer id;

    @Schema(description = "ID món ăn", example = "10")
    private Integer menuItemId;

    @Schema(description = "ID nguyên liệu master", example = "5")
    private Integer ingredientMasterId;

    @Schema(description = "Tên nguyên liệu master", example = "Thịt bò Úc")
    private String ingredientMasterName;

    @Schema(description = "Định lượng tiêu chuẩn cần dùng", example = "0.25")
    private double quantityRequired;
}
