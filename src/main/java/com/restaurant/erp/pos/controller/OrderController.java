package com.restaurant.erp.pos.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.pos.dto.request.CreateOrderItemRequest;
import com.restaurant.erp.pos.dto.request.CreateOrderRequest;
import com.restaurant.erp.pos.dto.response.OrderItemResponse;
import com.restaurant.erp.pos.dto.response.OrderResponse;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.service.OrderItemService;
import com.restaurant.erp.pos.service.OrderService;
import com.restaurant.erp.pos.service.OrderSplitMergeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "POS & Orders", description = "APIs for Point-of-Sale order operations")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderSplitMergeService orderSplitMergeService;

    @GetMapping
    @Operation(summary = "Lấy danh sách các đơn hàng")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderResponses()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết đơn hàng theo ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderResponseById(id)));
    }

    @PostMapping
    @Operation(summary = "Tạo đơn hàng mới tại bàn với validation")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(request), "Tạo đơn hàng thành công"));
    }

    @PostMapping("/{orderId}/items")
    @Operation(summary = "Thêm món vào đơn hàng")
    public ResponseEntity<ApiResponse<OrderItemResponse>> addItemToOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody CreateOrderItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderItemService.addItemToOrder(orderId, request), "Thêm món vào đơn hàng thành công"));
    }

    @PutMapping("/items/{itemId}/status")
    @Operation(summary = "Cập nhật trạng thái chế biến của món ăn (PENDING, COOKING, READY, SERVED, CANCELLED)")
    public ResponseEntity<ApiResponse<OrderItemResponse>> updateItemStatus(
            @PathVariable Long itemId,
            @RequestParam OrderItem.OrderItemStatus status) {
        return ResponseEntity.ok(ApiResponse.success(orderItemService.updateItemStatusResponse(itemId, status), "Cập nhật trạng thái món thành công"));
    }

    @PostMapping("/{id}/split")
    @Operation(summary = "Tách món sang bàn mới")
    public ResponseEntity<ApiResponse<OrderResponse>> splitOrder(
            @PathVariable Long id,
            @RequestParam List<Long> itemIds,
            @RequestParam Long newTableId) {
        Order newOrder = orderSplitMergeService.splitOrder(id, itemIds, newTableId);
        return ResponseEntity.ok(ApiResponse.success(orderService.mapToOrderResponse(newOrder), "Tách đơn hàng thành công"));
    }

    @PostMapping("/{id}/merge")
    @Operation(summary = "Gộp hai đơn hàng vào một")
    public ResponseEntity<ApiResponse<Void>> mergeOrders(
            @PathVariable Long id,
            @RequestParam Long destinationOrderId) {
        orderSplitMergeService.mergeOrders(id, destinationOrderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Gộp đơn hàng thành công"));
    }
}
