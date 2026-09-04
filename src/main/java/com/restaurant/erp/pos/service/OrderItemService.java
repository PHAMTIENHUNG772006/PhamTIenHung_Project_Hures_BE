package com.restaurant.erp.pos.service;

import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.menu.entity.MenuItem;
import com.restaurant.erp.menu.repository.MenuItemRepository;
import com.restaurant.erp.pos.dto.OrderDto;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.fsm.OrderItemStatusValidator;
import com.restaurant.erp.pos.repository.OrderItemRepository;
import com.restaurant.erp.pos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderService orderService;
    private final OrderItemStatusValidator statusValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderDto.OrderItemDto addItemToOrder(Long orderId, OrderDto.OrderItemDto itemDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.OPEN) {
            throw new BusinessException("Cannot add items to a closed or cancelled order");
        }

        MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));

        OrderItem item = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(itemDto.getQuantity())
                .price(menuItem.getPrice())
                .note(itemDto.getNote())
                .status(OrderItem.OrderItemStatus.PENDING)
                .build();

        OrderItem saved = orderItemRepository.save(item);
        order.getItems().add(saved);
        orderService.recalculateOrderTotals(order);

        // Notify events
        eventPublisher.publishEvent(saved);

        return mapToDto(saved);
    }

    @Transactional
    public OrderDto.OrderItemDto updateItemStatus(Long itemId, OrderItem.OrderItemStatus targetStatus) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found"));

        if (!statusValidator.isValidTransition(item.getStatus(), targetStatus)) {
            throw new BusinessException("Invalid status transition from " + item.getStatus() + " to " + targetStatus);
        }

        item.setStatus(targetStatus);
        OrderItem saved = orderItemRepository.save(item);

        if (targetStatus == OrderItem.OrderItemStatus.CANCELLED) {
            orderService.recalculateOrderTotals(item.getOrder());
        }

        // Notify events
        eventPublisher.publishEvent(saved);

        return mapToDto(saved);
    }

    private OrderDto.OrderItemDto mapToDto(OrderItem item) {
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
