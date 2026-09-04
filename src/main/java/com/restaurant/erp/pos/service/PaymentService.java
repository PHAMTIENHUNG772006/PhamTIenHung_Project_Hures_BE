package com.restaurant.erp.pos.service;

import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.pos.dto.PaymentDto;
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
