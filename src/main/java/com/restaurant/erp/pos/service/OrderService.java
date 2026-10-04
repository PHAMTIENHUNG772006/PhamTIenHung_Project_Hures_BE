package com.restaurant.erp.pos.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.menu.entity.MenuItem;
import com.restaurant.erp.menu.repository.MenuItemRepository;
import com.restaurant.erp.pos.dto.OrderDto;
import com.restaurant.erp.pos.dto.request.CreateOrderItemRequest;
import com.restaurant.erp.pos.dto.request.CreateOrderRequest;
import com.restaurant.erp.pos.dto.response.OrderItemResponse;
import com.restaurant.erp.pos.dto.response.OrderResponse;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.repository.OrderRepository;
import com.restaurant.erp.table.entity.DiningTable;
import com.restaurant.erp.table.repository.TableRepository;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            return branchRepository.findAll().stream().findFirst().map(Branch::getId).orElse(1);
        }
        return branchId;
    }

    public List<OrderResponse> getOrderResponses() {
        return orderRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrders() {
        return orderRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderResponseById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại: " + id));
        return mapToOrderResponse(order);
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToDto(order);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Integer branchId = request.getBranchId() != null ? request.getBranchId() : getActiveBranchId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Chi nhánh không tồn tại: " + branchId));

        User waiter = null;
        if (request.getWaiterId() != null) {
            waiter = userRepository.findById(request.getWaiterId()).orElse(null);
        }

        User cashier = null;
        if (request.getCashierId() != null) {
            cashier = userRepository.findById(request.getCashierId()).orElse(null);
        }

        Order order = Order.builder()
                .branch(branch)
                .tableId(request.getTableId())
                .waiter(waiter)
                .cashier(cashier)
                .bookingId(request.getBookingId())
                .status(Order.OrderStatus.OPEN)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
                .taxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        Order saved = orderRepository.save(order);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreateOrderItemRequest itemReq : request.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Món ăn không tồn tại: " + itemReq.getMenuItemId()));
                BigDecimal price = itemReq.getPrice() != null ? itemReq.getPrice() : menuItem.getPrice();
                OrderItem item = OrderItem.builder()
                        .order(saved)
                        .menuItem(menuItem)
                        .quantity(itemReq.getQuantity())
                        .price(price)
                        .note(itemReq.getNote())
                        .status(OrderItem.OrderItemStatus.PENDING)
                        .build();
                saved.getItems().add(item);
            }
            recalculateOrderTotals(saved);
        }

        return mapToOrderResponse(saved);
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

    public OrderResponse mapToOrderResponse(Order order) {
        if (order == null) return null;
        String tableName = null;
        if (order.getTableId() != null) {
            tableName = tableRepository.findById(order.getTableId().intValue())
                    .map(DiningTable::getTableNumber)
                    .orElse("Bàn #" + order.getTableId());
        }

        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(this::mapToOrderItemResponse).collect(Collectors.toList())
                : new ArrayList<>();

        return OrderResponse.builder()
                .id(order.getId())
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .tableId(order.getTableId())
                .tableName(tableName)
                .waiterId(order.getWaiter() != null ? order.getWaiter().getId() : null)
                .waiterName(order.getWaiter() != null ? order.getWaiter().getFullName() : null)
                .cashierId(order.getCashier() != null ? order.getCashier().getId() : null)
                .bookingId(order.getBookingId())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public OrderItemResponse mapToOrderItemResponse(OrderItem item) {
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
