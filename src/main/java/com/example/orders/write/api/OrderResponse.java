package com.example.orders.write.api;

import com.example.orders.shared.domain.Order;
import com.example.orders.shared.domain.OrderItem;
import com.example.orders.shared.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    String id,
    String customerId,
    LocalDateTime createdAt,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> items
) {
    public static OrderResponse fromDomain(Order order) {
        return new OrderResponse(
                order.getId().getValue(),
                order.getCustomerId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount().getAmount(),
                order.getItems().stream()
                        .map(OrderItemResponse::fromDomain)
                        .toList()
        );
    }

    public record OrderItemResponse(
        String id,
        String productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal totalPrice
    ) {
        public static OrderItemResponse fromDomain(OrderItem item) {
            return new OrderItemResponse(
                    item.getId(),
                    item.getProductId(),
                    item.getProductName(),
                    item.getUnitPrice().getAmount(),
                    item.getQuantity(),
                    item.getTotalPrice().getAmount()
            );
        }
    }
}