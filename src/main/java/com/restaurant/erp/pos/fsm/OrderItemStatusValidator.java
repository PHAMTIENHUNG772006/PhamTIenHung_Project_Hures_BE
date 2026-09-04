package com.restaurant.erp.pos.fsm;

import com.restaurant.erp.pos.entity.OrderItem.OrderItemStatus;
import org.springframework.stereotype.Component;

@Component
public class OrderItemStatusValidator {

    public boolean isValidTransition(OrderItemStatus current, OrderItemStatus target) {
        if (current == target) {
            return true;
        }

        switch (current) {
            case PENDING:
                return target == OrderItemStatus.COOKING || target == OrderItemStatus.CANCELLED;
            case COOKING:
                return target == OrderItemStatus.READY || target == OrderItemStatus.CANCELLED;
            case READY:
                return target == OrderItemStatus.SERVED;
            case SERVED:
            case CANCELLED:
            default:
                return false;
        }
    }
}
