package com.restaurant.erp.kds.service;

import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.pos.dto.OrderDto;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KdsService {

    private final OrderRepository orderRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            return 1;
        }
        return branchId;
    }

    public List<OrderDto> getActiveKitchenTickets() {
        return getActiveKitchenTickets(getActiveBranchId());
    }

    public List<OrderDto> getActiveKitchenTickets(Integer branchId) {
        Integer targetBranchId = branchId != null ? branchId : getActiveBranchId();
        return orderRepository.findByBranchId(targetBranchId).stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.OPEN 
                        || order.getStatus() == Order.OrderStatus.BILL_REQUESTED)
                .map(this::mapToKdsTicket)
                .filter(ticket -> !ticket.getItems().isEmpty())
                .collect(Collectors.toList());
    }

    private OrderDto mapToKdsTicket(Order order) {
        List<OrderItem> activeItems = order.getItems().stream()
                .filter(item -> item.getStatus() == OrderItem.OrderItemStatus.PENDING 
                        || item.getStatus() == OrderItem.OrderItemStatus.COOKING
                        || item.getStatus() == OrderItem.OrderItemStatus.READY)
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .branchId(order.getBranch().getId())
                .tableId(order.getTableId())
                .waiterId(order.getWaiter() != null ? order.getWaiter().getId() : null)
                .status(order.getStatus())
                .items(activeItems.stream().map(this::mapItemToDto).collect(Collectors.toList()))
                .build();
    }

    private OrderDto.OrderItemDto mapItemToDto(OrderItem item) {
        return OrderDto.OrderItemDto.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .note(item.getNote())
                .status(item.getStatus())
                .build();
    }
}
