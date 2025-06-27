package com.example.orders.write.api;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
    String customerId,
    List<OrderItemRequest> items
) {
    public record OrderItemRequest(
        String productId,
        String productName,
        BigDecimal unitPrice,
        int quantity
    ) {}
}