package com.example.shared.integration;

import com.example.shared.domain.DomainEvent;

import java.util.List;

/**
 * Integration event for cross-context communication.
 * This is NOT a domain event - it's specifically for integration between bounded contexts.
 */
public class OrderPlacedIntegrationEvent extends DomainEvent {
    private final String orderId;
    private final String customerId;
    private final List<ProductQuantityData> productsToReduce;

    public OrderPlacedIntegrationEvent(String orderId, String customerId, List<ProductQuantityData> productsToReduce) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
        this.productsToReduce = productsToReduce;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<ProductQuantityData> getProductsToReduce() {
        return productsToReduce;
    }

    /**
     * Simple data structure for integration - no domain logic
     */
    public static class ProductQuantityData {
        private final String productId;
        private final int quantity;

        public ProductQuantityData(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}