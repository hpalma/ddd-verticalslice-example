package com.example.products.domain;

import com.example.shared.domain.Entity;

import java.util.Objects;

public class Product extends Entity<ProductId> {
    private String name;
    private String description;
    private Price price;
    private int stockQuantity;
    private boolean active;

    public Product(ProductId id, String name, String description, Price price, int stockQuantity) {
        super(id);
        this.name = Objects.requireNonNull(name, "Product name cannot be null");
        this.description = Objects.requireNonNull(description, "Product description cannot be null");
        this.price = Objects.requireNonNull(price, "Product price cannot be null");
        
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = stockQuantity;
        this.active = true;
    }

    public void updatePrice(Price newPrice) {
        this.price = Objects.requireNonNull(newPrice, "Price cannot be null");
    }

    public void updateStock(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = newQuantity;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean isAvailable() {
        return active && stockQuantity > 0;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Price getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public boolean isActive() {
        return active;
    }
}