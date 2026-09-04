package com.restaurant.erp.pos.service;

import com.restaurant.erp.pos.entity.Order;
import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.repository.OrderItemRepository;
import com.restaurant.erp.pos.repository.OrderRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSplitMergeService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    @Transactional
    public Order splitOrder(long originalOrderId, List<Long> orderItemIds, long newTableId) {
        Order originalOrder = orderRepository.findById(originalOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order newOrder = Order.builder()
                .branch(originalOrder.getBranch())
                .tableId(newTableId)
                .waiter(originalOrder.getWaiter())
                .status(Order.OrderStatus.OPEN)
                .build();
        newOrder = orderRepository.save(newOrder);

        List<OrderItem> itemsToMove = orderItemRepository.findAllById(orderItemIds);
        for (OrderItem item : itemsToMove) {
            if (item.getOrder().getId() != originalOrderId) {
                throw new RuntimeException("Item does not belong to the original order");
            }
            item.setOrder(newOrder);
        }
        orderItemRepository.saveAll(itemsToMove);

        // Recalculate totals for both orders
        orderService.recalculateOrderTotals(originalOrder);
        orderService.recalculateOrderTotals(newOrder);

        return newOrder;
    }

    @Transactional
    public void mergeOrders(long sourceOrderId, long destinationOrderId) {
        Order sourceOrder = orderRepository.findById(sourceOrderId)
                .orElseThrow(() -> new RuntimeException("Source order not found"));
        Order destinationOrder = orderRepository.findById(destinationOrderId)
                .orElseThrow(() -> new RuntimeException("Destination order not found"));

        List<OrderItem> sourceOrderItems = sourceOrder.getItems();
        for (OrderItem item : sourceOrderItems) {
            item.setOrder(destinationOrder);
        }
        orderItemRepository.saveAll(sourceOrderItems);
        
        // Recalculate totals for destination
        orderService.recalculateOrderTotals(destinationOrder);
        
        orderRepository.delete(sourceOrder);
    }
}