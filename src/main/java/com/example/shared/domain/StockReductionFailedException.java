package com.example.shared.domain;

public class StockReductionFailedException extends RuntimeException {
    private final String productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    public StockReductionFailedException(String productId, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d", 
                           productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
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
}