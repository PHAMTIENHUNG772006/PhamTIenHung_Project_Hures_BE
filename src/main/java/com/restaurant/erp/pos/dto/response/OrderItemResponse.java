package com.restaurant.erp.pos.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.pos.entity.OrderItem.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết món ăn trong đơn hàng")
public class OrderItemResponse {

    @Schema(description = "ID món trong đơn hàng", example = "10")
    private Long id;

    @Schema(description = "ID món ăn trong thực đơn", example = "1")
    private Integer menuItemId;

    @Schema(description = "Tên món ăn", example = "Bò bít tết")
    private String menuItemName;

    @Schema(description = "Số lượng", example = "2")
    private Integer quantity;

    @Schema(description = "Đơn giá", example = "250000")
    private BigDecimal price;

    @Schema(description = "Thành tiền", example = "500000")
    private BigDecimal subtotal;

    @Schema(description = "Ghi chú chế biến món ăn")
    private String note;

    @Schema(description = "Trạng thái chế biến món (PENDING, COOKING, READY, SERVED, CANCELLED)")
    private OrderItemStatus status;
}
