package com.restaurant.erp.kds.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KdsEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishItemStatus(Long orderItemId, String status) {
        log.info("Sending WebSocket notification for item {} status {}", orderItemId, status);
        messagingTemplate.convertAndSend("/topic/kds/status", "{\"orderItemId\":" + orderItemId + ",\"status\":\"" + status + "\"}");
    }

    public void publishNewTicket(Long orderId) {
        log.info("Sending WebSocket notification for new order ticket {}", orderId);
        messagingTemplate.convertAndSend("/topic/kds/tickets", "{\"orderId\":" + orderId + ",\"action\":\"NEW\"}");
    }
}
