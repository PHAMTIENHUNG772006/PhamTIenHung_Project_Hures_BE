package com.restaurant.erp.pos.dto;

import com.restaurant.erp.pos.entity.Order.OrderStatus;
import com.restaurant.erp.pos.entity.OrderItem.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Integer branchId;
    private Long tableId;
    private Long waiterId;
    private Long cashierId;
    private Long bookingId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private List<OrderItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long id;
        private Integer menuItemId;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal price;
        private String note;
        private OrderItemStatus status;
    }
}
