package com.restaurant.erp.pos.controller;

import com.restaurant.erp.common.annotation.Idempotent;
import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.pos.dto.request.ProcessPaymentRequest;
import com.restaurant.erp.pos.dto.response.PaymentResponse;
import com.restaurant.erp.pos.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for billing and payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping({"", "/process"})
    @Idempotent
    @Operation(summary = "Thanh toán hóa đơn đơn hàng với validation")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.processPayment(request), "Thanh toán thành công"));
    }
}
