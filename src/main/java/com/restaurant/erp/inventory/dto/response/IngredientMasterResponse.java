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
@Schema(description = "Thông tin nguyên liệu master trong danh mục tổng")
public class IngredientMasterResponse {

    @Schema(description = "ID nguyên liệu master", example = "1")
    private Integer id;

    @Schema(description = "Tên nguyên liệu", example = "Thịt bò Úc")
    private String name;

    @Schema(description = "Đơn vị tính tiêu chuẩn", example = "kg")
    private String unit;
}
