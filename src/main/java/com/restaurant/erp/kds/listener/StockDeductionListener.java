package com.restaurant.erp.kds.listener;

import com.restaurant.erp.inventory.entity.StockTransaction;
import com.restaurant.erp.inventory.repository.StockTransactionRepository;
import com.restaurant.erp.kds.websocket.KdsEventPublisher;
import com.restaurant.erp.pos.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockDeductionListener {

    private final StockTransactionRepository stockTransactionRepository;
    private final KdsEventPublisher kdsEventPublisher;

    @EventListener
    public void handleOrderItemStatusUpdate(OrderItem orderItem) {
        if (orderItem.getStatus() == OrderItem.OrderItemStatus.READY) {
            log.info("OrderItem {} status is READY. Scanning for negative stock transactions...", orderItem.getId());
            List<StockTransaction> txs = stockTransactionRepository.findByReferenceOrderId(orderItem.getOrder().getId());
            for (StockTransaction tx : txs) {
                String searchStr = "Order Item #" + orderItem.getId();
                if (tx.getNote() != null && tx.getNote().contains(searchStr) && tx.getNote().contains("[CẢNH BÁO ÂM KHO]")) {
                    log.warn("INSUFFICIENT STOCK WARNING: {}", tx.getNote());
                    kdsEventPublisher.publishItemStatus(orderItem.getId(), "READY_WARNING_INSUFFICIENT_STOCK");
                }
            }
        }
    }
}
