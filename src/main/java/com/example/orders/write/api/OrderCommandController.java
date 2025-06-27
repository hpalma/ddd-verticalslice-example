package com.example.orders.write.api;

import com.example.orders.shared.domain.Order;
import com.example.orders.shared.domain.OrderId;
import com.example.orders.shared.domain.OrderRepository;
import com.example.orders.write.application.OrderCommandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dedicated to write operations.
 * Focused on command processing and business logic execution.
 */
@RestController
@RequestMapping("/api/orders/command")
public class OrderCommandController {
    private final OrderCommandService commandService;
    private final OrderRepository orderRepository; // For immediate response after creation

    public OrderCommandController(OrderCommandService commandService, OrderRepository orderRepository) {
        this.commandService = commandService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderId orderId = commandService.createOrder(request);
        Order order = orderRepository.findById(orderId).orElseThrow();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderResponse.fromDomain(order));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable String orderId) {
        commandService.confirmOrder(OrderId.of(orderId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        commandService.cancelOrder(OrderId.of(orderId));
        return ResponseEntity.ok().build();
    }
}