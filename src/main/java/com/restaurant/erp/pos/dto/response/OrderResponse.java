package com.restaurant.erp.pos.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.pos.entity.Order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết đơn hàng (hóa đơn)")
public class OrderResponse {

    @Schema(description = "Mã đơn hàng", example = "101")
    private Long id;

    @Schema(description = "ID chi nhánh", example = "1")
    private Integer branchId;

    @Schema(description = "ID bàn ăn", example = "3")
    private Long tableId;

    @Schema(description = "Tên bàn ăn", example = "Bàn 03")
    private String tableName;

    @Schema(description = "ID nhân viên phục vụ")
    private Long waiterId;

    @Schema(description = "Tên nhân viên phục vụ")
    private String waiterName;

    @Schema(description = "ID nhân viên thu ngân")
    private Long cashierId;

    @Schema(description = "ID lượt đặt trước (nếu có)")
    private Long bookingId;

    @Schema(description = "Tổng tiền món", example = "500000")
    private BigDecimal totalAmount;

    @Schema(description = "Số tiền giảm giá", example = "50000")
    private BigDecimal discountAmount;

    @Schema(description = "Tiền thuế VAT", example = "45000")
    private BigDecimal taxAmount;

    @Schema(description = "Tổng tiền thanh toán cuối cùng", example = "495000")
    private BigDecimal finalAmount;

    @Schema(description = "Trạng thái đơn hàng (PENDING, PAID, CANCELLED)")
    private OrderStatus status;

    @Schema(description = "Danh sách các món ăn trong đơn hàng")
    private List<OrderItemResponse> items;

    @Schema(description = "Thời gian tạo đơn")
    private ZonedDateTime createdAt;
}
