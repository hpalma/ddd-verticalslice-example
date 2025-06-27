package com.example.orders.shared.domain.events;

import com.example.orders.shared.domain.OrderId;
import com.example.shared.domain.DomainEvent;

/**
 * Domain event internal to the Orders bounded context.
 * Used for orders-specific business logic and event handlers.
 */
public class OrderCreated extends DomainEvent {
    private final OrderId orderId;
    private final String customerId;

    public OrderCreated(OrderId orderId, String customerId) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }
}