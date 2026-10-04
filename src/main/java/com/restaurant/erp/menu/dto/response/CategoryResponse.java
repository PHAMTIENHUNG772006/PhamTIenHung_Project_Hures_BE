package com.restaurant.erp.menu.dto.response;

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
@Schema(description = "Thông tin chi tiết danh mục món ăn")
public class CategoryResponse {

    @Schema(description = "ID danh mục", example = "1")
    private Integer id;

    @Schema(description = "Tên danh mục", example = "Món Khai Vị")
    private String name;

    @Schema(description = "Thứ tự hiển thị", example = "1")
    private Integer displayOrder;

    @Schema(description = "Số lượng món ăn trong danh mục", example = "8")
    private Integer itemCount;
}
