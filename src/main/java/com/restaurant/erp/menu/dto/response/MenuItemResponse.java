package com.restaurant.erp.menu.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết món ăn")
public class MenuItemResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID món ăn", example = "1")
    private Integer id;

    @Schema(description = "ID danh mục", example = "2")
    private Integer categoryId;

    @Schema(description = "Tên danh mục", example = "Món Chính")
    private String categoryName;

    @Schema(description = "Tên món ăn", example = "Bò bít tết sốt tiêu đen")
    private String name;

    @Schema(description = "Mô tả món ăn")
    private String description;

    @Schema(description = "Giá bán món ăn (VNĐ)", example = "250000")
    private BigDecimal price;

    @Schema(description = "URL hình ảnh món ăn")
    private String imageUrl;

    @Schema(description = "Trạng thái sẵn sàng phục vụ", example = "true")
    private Boolean isAvailable;
}
