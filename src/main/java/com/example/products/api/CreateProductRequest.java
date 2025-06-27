package com.example.products.api;

import java.math.BigDecimal;

public record CreateProductRequest(
    String name,
    String description,
    BigDecimal price,
    int stockQuantity
) {}