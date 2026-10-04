package com.restaurant.erp.menu.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu cập nhật danh mục món ăn")
public class UpdateCategoryRequest {

    @Schema(description = "Tên danh mục", example = "Món Khai Vị", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 50, message = "Tên danh mục từ 2 đến 50 ký tự")
    private String name;

    @Schema(description = "Thứ tự sắp xếp hiển thị", example = "1")
    @Min(value = 0, message = "Thứ tự hiển thị không được âm")
    private Integer displayOrder;
}
