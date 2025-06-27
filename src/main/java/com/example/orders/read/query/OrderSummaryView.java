package com.example.orders.read.query;

import com.example.orders.shared.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lightweight read model for order lists and summaries.
 * Optimized for performance - no item details.
 */
public class OrderSummaryView {
    private final String orderId;
    private final String customerId;
    private final String customerName;
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final int totalItems;

    public OrderSummaryView(String orderId, String customerId, String customerName,
                           LocalDateTime createdAt, OrderStatus status, 
                           BigDecimal totalAmount, int totalItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.createdAt = createdAt;
        this.status = status;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public int getTotalItems() { return totalItems; }
}