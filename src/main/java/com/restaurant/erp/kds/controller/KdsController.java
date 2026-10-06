package com.restaurant.erp.kds.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.kds.service.KdsService;
import com.restaurant.erp.pos.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "KDS Management", description = "APIs for Kitchen Display System and WebSocket fallbacks")
public class KdsController {

    private final KdsService kdsService;

    @GetMapping({"/api/v1/kds/orders/active", "/api/kds/orders/active"})
    @Operation(summary = "Đồng bộ danh sách món ăn đang nấu tại bếp (Fallback khi WebSocket reconnect)")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getActiveKitchenOrders(
            @RequestParam(required = false) Integer branchId) {
        List<OrderDto> activeTickets = kdsService.getActiveKitchenTickets(branchId);
        return ResponseEntity.ok(ApiResponse.success(activeTickets, "Đồng bộ danh sách món đang chế biến thành công"));
    }
}
