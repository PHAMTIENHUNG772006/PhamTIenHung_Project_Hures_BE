package com.restaurant.erp.kds.websocket;

import com.restaurant.erp.pos.entity.OrderItem;
import com.restaurant.erp.pos.service.OrderItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class KdsWebSocketController {

    private final OrderItemService orderItemService;
    private final KdsEventPublisher kdsEventPublisher;

    @MessageMapping("/kds/status")
    public void handleStatusChange(KdsStatusUpdatePayload payload) {
        OrderItem.OrderItemStatus status = OrderItem.OrderItemStatus.valueOf(payload.getStatus());
        orderItemService.updateItemStatus(payload.getOrderItemId(), status);
        
        kdsEventPublisher.publishItemStatus(payload.getOrderItemId(), payload.getStatus());
    }

    @Getter
    @Setter
    public static class KdsStatusUpdatePayload {
        private Long orderItemId;
        private String status;
    }
}
