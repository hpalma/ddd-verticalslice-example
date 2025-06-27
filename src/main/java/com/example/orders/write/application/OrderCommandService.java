package com.example.orders.write.application;

import com.example.orders.write.api.CreateOrderRequest;
import com.example.orders.shared.domain.*;
import com.example.orders.shared.domain.events.OrderCreated;
import com.example.shared.domain.DomainEventPublisher;
import com.example.shared.integration.OrderPlacedIntegrationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Command service focused purely on write operations.
 * Optimized for business logic execution and consistency.
 */
@Service
@Transactional
public class OrderCommandService {
    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    public OrderCommandService(OrderRepository orderRepository, DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public OrderId createOrder(CreateOrderRequest request) {
        OrderId orderId = OrderId.generate();
        
        List<OrderItem> orderItems = request.items().stream()
                .map(item -> new OrderItem(
                        UUID.randomUUID().toString(),
                        item.productId(),
                        item.productName(),
                        Money.of(item.unitPrice(), "USD"),
                        item.quantity()
                ))
                .toList();

        Order order = new Order(orderId, request.customerId(), orderItems);
        orderRepository.save(order);
        
        // Publish domain event (internal to orders context)
        eventPublisher.publishEvent(new OrderCreated(orderId, request.customerId()));
        
        // Publish integration event (for other contexts)
        List<OrderPlacedIntegrationEvent.ProductQuantityData> productsToReduce = orderItems.stream()
                .map(item -> new OrderPlacedIntegrationEvent.ProductQuantityData(item.getProductId(), item.getQuantity()))
                .toList();
        
        eventPublisher.publishEvent(new OrderPlacedIntegrationEvent(
                orderId.getValue(), 
                request.customerId(), 
                productsToReduce
        ));
        
        return orderId;
    }

    public void confirmOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        order.confirm();
        orderRepository.save(order);
        
        // Could publish OrderConfirmed event here
    }

    public void cancelOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        order.cancel();
        orderRepository.save(order);
        
        // Could publish OrderCancelled event here
    }
}