package com.example.orders.read.application;

import com.example.orders.shared.domain.OrderStatus;
import com.example.orders.read.query.OrderSummaryView;
import com.example.orders.read.query.OrderView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for read-optimized order queries.
 * Implementations can use different storage optimizations.
 */
public interface OrderViewRepository {
    Optional<OrderView> findByOrderId(String orderId);
    
    List<OrderSummaryView> findSummariesByCustomerId(String customerId);
    List<OrderSummaryView> findSummariesByStatus(OrderStatus status);
    List<OrderSummaryView> findSummariesByDateRange(LocalDateTime from, LocalDateTime to);
    List<OrderSummaryView> findRecentOrderSummaries(int limit);
    
    OrderQueryService.OrderAnalytics getOrderAnalytics(String customerId);
}