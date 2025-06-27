package com.example.products.api;

import com.example.products.domain.Product;

import java.math.BigDecimal;

public record ProductResponse(
    String id,
    String name,
    String description,
    BigDecimal price,
    int stockQuantity,
    boolean active,
    boolean available
) {
    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(
                product.getId().getValue(),
                product.getName(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getStockQuantity(),
                product.isActive(),
                product.isAvailable()
        );
    }
}