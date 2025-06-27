package com.example.orders.shared.domain;

import com.example.shared.domain.Entity;

import java.util.Objects;

public class OrderItem extends Entity<String> {
    private final String productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;

    public OrderItem(String id, String productId, String productName, Money unitPrice, int quantity) {
        super(id);
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public Money getTotalPrice() {
        return unitPrice.multiply(quantity);
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}