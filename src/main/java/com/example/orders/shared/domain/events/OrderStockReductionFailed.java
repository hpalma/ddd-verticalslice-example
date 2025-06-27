package com.example.orders.shared.domain.events;

import com.example.orders.shared.domain.OrderId;
import com.example.shared.domain.DomainEvent;

public class OrderStockReductionFailed extends DomainEvent {
    private final OrderId orderId;
    private final String productId;
    private final int requestedQuantity;
    private final int availableQuantity;
    private final String reason;

    public OrderStockReductionFailed(OrderId orderId, String productId, 
                                   int requestedQuantity, int availableQuantity, String reason) {
        super();
        this.orderId = orderId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
        this.reason = reason;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public String getReason() {
        return reason;
    }
}