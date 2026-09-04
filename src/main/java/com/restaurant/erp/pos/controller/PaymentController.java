package com.restaurant.erp.pos.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.pos.dto.PaymentDto;
import com.restaurant.erp.pos.service.PaymentService;
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
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(@Valid @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.processPayment(dto), "Payment processed successfully"));
    }
}
