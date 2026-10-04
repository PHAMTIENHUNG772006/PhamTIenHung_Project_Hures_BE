package com.restaurant.erp.pos.service;

import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.pos.dto.PaymentDto;
import com.restaurant.erp.pos.dto.request.ProcessPaymentRequest;
import com.restaurant.erp.pos.dto.response.PaymentResponse;
import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.Payment;
import com.restaurant.erp.pos.repository.OrderRepository;
import com.restaurant.erp.pos.repository.PaymentRepository;
import com.restaurant.erp.table.entity.DiningTable;
import com.restaurant.erp.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại: " + request.getOrderId()));

        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new BusinessException("Đơn hàng này đã được thanh toán trước đó");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .method(request.getMethod())
                .transactionReference(request.getTransactionReference())
                .paidAt(ZonedDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);

        if (order.getTableId() != null) {
            tableRepository.findById(order.getTableId().intValue()).ifPresent(table -> {
                table.setStatus(DiningTable.TableStatus.NEEDS_CLEANING);
                tableRepository.save(table);
            });
        }

        return mapToPaymentResponse(saved);
    }

    @Transactional
    public PaymentDto processPayment(PaymentDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new BusinessException("Order is already paid");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .transactionReference(dto.getTransactionReference())
                .paidAt(ZonedDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);

        if (order.getTableId() != null) {
            tableRepository.findById(order.getTableId().intValue()).ifPresent(table -> {
                table.setStatus(DiningTable.TableStatus.NEEDS_CLEANING);
                tableRepository.save(table);
            });
        }

        return mapToDto(saved);
    }

    public PaymentResponse mapToPaymentResponse(Payment payment) {
        if (payment == null) return null;
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .transactionReference(payment.getTransactionReference())
                .paidAt(payment.getPaidAt())
                .build();
    }

    private PaymentDto mapToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .transactionReference(payment.getTransactionReference())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
