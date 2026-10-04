package com.restaurant.erp.pos.service;

import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.menu.entity.MenuItem;
import com.restaurant.erp.menu.repository.MenuItemRepository;
import com.restaurant.erp.pos.dto.OrderDto;
import com.restaurant.erp.pos.dto.request.CreateOrderItemRequest;
import com.restaurant.erp.pos.dto.response.OrderItemResponse;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.fsm.OrderItemStatusValidator;
import com.restaurant.erp.pos.repository.OrderItemRepository;
import com.restaurant.erp.pos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
    public OrderItemResponse addItemToOrder(Long orderId, CreateOrderItemRequest itemRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại: " + orderId));

        if (order.getStatus() != Order.OrderStatus.OPEN) {
            throw new BusinessException("Không thể thêm món vào đơn hàng đã đóng hoặc bị hủy");
        }

        MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn không tồn tại: " + itemRequest.getMenuItemId()));

        BigDecimal price = itemRequest.getPrice() != null ? itemRequest.getPrice() : menuItem.getPrice();
        OrderItem item = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(itemRequest.getQuantity())
                .price(price)
                .note(itemRequest.getNote())
                .status(OrderItem.OrderItemStatus.PENDING)
                .build();

        OrderItem saved = orderItemRepository.save(item);
        order.getItems().add(saved);
        orderService.recalculateOrderTotals(order);

        eventPublisher.publishEvent(saved);

        return mapToItemResponse(saved);
    }

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

    @Transactional
    public OrderItemResponse updateItemStatusResponse(Long itemId, OrderItem.OrderItemStatus targetStatus) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Món trong đơn hàng không tồn tại: " + itemId));

        if (!statusValidator.isValidTransition(item.getStatus(), targetStatus)) {
            throw new BusinessException("Trạng thái chuyển đổi không hợp lệ từ " + item.getStatus() + " sang " + targetStatus);
        }

        item.setStatus(targetStatus);
        OrderItem saved = orderItemRepository.save(item);

        if (targetStatus == OrderItem.OrderItemStatus.CANCELLED) {
            orderService.recalculateOrderTotals(item.getOrder());
        }

        eventPublisher.publishEvent(saved);

        return mapToItemResponse(saved);
    }

    public OrderItemResponse mapToItemResponse(OrderItem item) {
        if (item == null) return null;
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1));
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem() != null ? item.getMenuItem().getId() : null)
                .menuItemName(item.getMenuItem() != null ? item.getMenuItem().getName() : null)
                .quantity(item.getQuantity())
                .price(price)
                .subtotal(subtotal)
                .note(item.getNote())
                .status(item.getStatus())
                .build();
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
