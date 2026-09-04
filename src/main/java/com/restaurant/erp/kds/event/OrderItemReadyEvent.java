package com.restaurant.erp.kds.event;

import com.restaurant.erp.pos.entity.OrderItem;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderItemReadyEvent extends ApplicationEvent {

    private final OrderItem orderItem;

    public OrderItemReadyEvent(Object source, OrderItem orderItem) {
        super(source);
        this.orderItem = orderItem;
    }
}
