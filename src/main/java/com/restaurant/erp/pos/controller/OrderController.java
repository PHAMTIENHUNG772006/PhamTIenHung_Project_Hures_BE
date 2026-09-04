package com.restaurant.erp.pos.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.pos.dto.OrderDto;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.service.OrderItemService;
import com.restaurant.erp.pos.service.OrderService;
import com.restaurant.erp.pos.service.OrderSplitMergeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderSplitMergeService orderSplitMergeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@Valid @RequestBody OrderDto dto) {
        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(dto), "Order created successfully"));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<OrderDto.OrderItemDto>> addItemToOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderItemDto itemDto) {
        return ResponseEntity.ok(ApiResponse.success(orderItemService.addItemToOrder(orderId, itemDto), "Item added to order"));
    }

    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<ApiResponse<OrderDto.OrderItemDto>> updateItemStatus(
            @PathVariable Long itemId,
            @RequestParam OrderItem.OrderItemStatus status) {
        return ResponseEntity.ok(ApiResponse.success(orderItemService.updateItemStatus(itemId, status), "Item status updated"));
    }

    @PostMapping("/{id}/split")
    public ResponseEntity<ApiResponse<OrderDto>> splitOrder(
            @PathVariable Long id,
            @RequestParam List<Long> itemIds,
            @RequestParam Long newTableId) {
        Order newOrder = orderSplitMergeService.splitOrder(id, itemIds, newTableId);
        return ResponseEntity.ok(ApiResponse.success(orderService.mapToDto(newOrder), "Order split successfully"));
    }

    @PostMapping("/{id}/merge")
    public ResponseEntity<ApiResponse<Void>> mergeOrders(
            @PathVariable Long id,
            @RequestParam Long destinationOrderId) {
        orderSplitMergeService.mergeOrders(id, destinationOrderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Orders merged successfully"));
    }
}
