package com.restaurant.erp.pos.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.pos.dto.OrderDto;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.repository.OrderRepository;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<OrderDto> getOrders() {
        return orderRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToDto(order);
    }

    @Transactional
    public OrderDto createOrder(OrderDto dto) {
        Branch branch = branchRepository.findById(getActiveBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        User waiter = null;
        if (dto.getWaiterId() != null) {
            waiter = userRepository.findById(dto.getWaiterId()).orElse(null);
        }

        Order order = Order.builder()
                .branch(branch)
                .tableId(dto.getTableId())
                .waiter(waiter)
                .bookingId(dto.getBookingId())
                .status(Order.OrderStatus.OPEN)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .build();

        return mapToDto(orderRepository.save(order));
    }

    @Transactional
    public void recalculateOrderTotals(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            if (item.getStatus() != OrderItem.OrderItemStatus.CANCELLED) {
                total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        order.setTotalAmount(total);

        // Compute 8% VAT tax
        BigDecimal tax = total.subtract(order.getDiscountAmount()).multiply(BigDecimal.valueOf(0.08));
        if (tax.compareTo(BigDecimal.ZERO) < 0) {
            tax = BigDecimal.ZERO;
        }
        order.setTaxAmount(tax);

        BigDecimal finalAmt = total.subtract(order.getDiscountAmount()).add(tax);
        if (finalAmt.compareTo(BigDecimal.ZERO) < 0) {
            finalAmt = BigDecimal.ZERO;
        }
        order.setFinalAmount(finalAmt);

        orderRepository.save(order);
    }

    public OrderDto mapToDto(Order order) {
        if (order == null) return null;
        return OrderDto.builder()
                .id(order.getId())
                .branchId(order.getBranch().getId())
                .tableId(order.getTableId())
                .waiterId(order.getWaiter() != null ? order.getWaiter().getId() : null)
                .cashierId(order.getCashier() != null ? order.getCashier().getId() : null)
                .bookingId(order.getBookingId())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .items(order.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList()))
                .build();
    }

    private OrderDto.OrderItemDto mapItemToDto(OrderItem item) {
        return OrderDto.OrderItemDto.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .note(item.getNote())
                .status(item.getStatus())
                .build();
    }
}
