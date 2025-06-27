package com.example.orders.read.query;

import com.example.orders.shared.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Read model optimized for queries.
 * Denormalized and flattened for fast reads.
 */
public class OrderView {
    private final String orderId;
    private final String customerId;
    private final String customerName; // Denormalized from customer context
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final String currency;
    private final int totalItems;
    private final List<OrderItemView> items;

    public OrderView(String orderId, String customerId, String customerName,
                    LocalDateTime createdAt, OrderStatus status, BigDecimal totalAmount,
                    String currency, int totalItems, List<OrderItemView> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.createdAt = createdAt;
        this.status = status;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.totalItems = totalItems;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getCurrency() { return currency; }
    public int getTotalItems() { return totalItems; }
    public List<OrderItemView> getItems() { return items; }

    public static class OrderItemView {
        private final String productId;
        private final String productName;
        private final String productCategory; // Denormalized from product context
        private final BigDecimal unitPrice;
        private final int quantity;
        private final BigDecimal totalPrice;

        public OrderItemView(String productId, String productName, String productCategory,
                           BigDecimal unitPrice, int quantity, BigDecimal totalPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productCategory = productCategory;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductCategory() { return productCategory; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public BigDecimal getTotalPrice() { return totalPrice; }
    }
}