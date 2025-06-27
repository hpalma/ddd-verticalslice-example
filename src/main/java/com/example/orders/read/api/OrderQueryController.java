package com.example.orders.read.api;

import com.example.orders.shared.domain.OrderStatus;
import com.example.orders.read.application.OrderQueryService;
import com.example.orders.read.query.OrderSummaryView;
import com.example.orders.read.query.OrderView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller dedicated to read operations.
 * Optimized for fast queries and different projections.
 */
@RestController
@RequestMapping("/api/orders/query")
public class OrderQueryController {
    private final OrderQueryService queryService;

    public OrderQueryController(OrderQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderView> getOrderDetails(@PathVariable String orderId) {
        return queryService.getOrderDetails(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderSummaryView> getCustomerOrders(@PathVariable String customerId) {
        return queryService.getOrdersByCustomer(customerId);
    }

    @GetMapping("/status/{status}")
    public List<OrderSummaryView> getOrdersByStatus(@PathVariable OrderStatus status) {
        return queryService.getOrdersByStatus(status);
    }

    @GetMapping("/recent")
    public List<OrderSummaryView> getRecentOrders(@RequestParam(defaultValue = "10") int limit) {
        return queryService.getRecentOrders(limit);
    }

    @GetMapping("/date-range")
    public List<OrderSummaryView> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return queryService.getOrdersByDateRange(from, to);
    }

    @GetMapping("/analytics/{customerId}")
    public OrderQueryService.OrderAnalytics getOrderAnalytics(@PathVariable String customerId) {
        return queryService.getOrderAnalytics(customerId);
    }
}