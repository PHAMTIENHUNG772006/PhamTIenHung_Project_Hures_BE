package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu đăng ký nguyên liệu gốc danh mục tổng")
public class CreateIngredientMasterRequest {

    @Schema(description = "Tên nguyên vật liệu", example = "Thịt bò Úc", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên danh mục nguyên liệu không được để trống")
    private String name;

    @Schema(description = "Đơn vị tính tiêu chuẩn (kg, g, lít, ml, quả...)", example = "kg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Đơn vị tính không được để trống")
    private String unit;
}
