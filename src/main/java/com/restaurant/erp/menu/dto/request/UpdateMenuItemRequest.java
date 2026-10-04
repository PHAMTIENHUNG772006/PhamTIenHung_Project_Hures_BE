package com.restaurant.erp.menu.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu cập nhật thông tin món ăn")
public class UpdateMenuItemRequest {

    @Schema(description = "ID danh mục món ăn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Danh mục món ăn không được để trống")
    private Integer categoryId;

    @Schema(description = "Tên món ăn", example = "Bò bít tết sốt tiêu đen", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên món ăn không được để trống")
    @Size(min = 2, max = 100, message = "Tên món ăn phải từ 2 đến 100 ký tự")
    private String name;

    @Schema(description = "Mô tả chi tiết món ăn")
    private String description;

    @Schema(description = "Giá bán món ăn (VNĐ)", example = "250000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Giá món ăn không được để trống")
    @Positive(message = "Giá món ăn phải lớn hơn 0")
    private BigDecimal price;

    @Schema(description = "URL ảnh món ăn")
    private String imageUrl;

    @Schema(description = "Món ăn đang sẵn sàng phục vụ", example = "true")
    private Boolean isAvailable;
}
