package com.restaurant.erp.kds.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TableChangedEvent extends ApplicationEvent {

    private final Long oldTableId;
    private final Long newTableId;
    private final Long orderId;

    public TableChangedEvent(Object source, Long oldTableId, Long newTableId, Long orderId) {
        super(source);
        this.oldTableId = oldTableId;
        this.newTableId = newTableId;
        this.orderId = orderId;
    }
}