package com.example.orders.read.application;

import com.example.orders.shared.domain.OrderStatus;
import com.example.orders.read.query.OrderSummaryView;
import com.example.orders.read.query.OrderView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Query service optimized for read operations.
 * Uses read-optimized repositories and projections.
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService {
    private final OrderViewRepository orderViewRepository;

    public OrderQueryService(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    public Optional<OrderView> getOrderDetails(String orderId) {
        return orderViewRepository.findByOrderId(orderId);
    }

    public List<OrderSummaryView> getOrdersByCustomer(String customerId) {
        return orderViewRepository.findSummariesByCustomerId(customerId);
    }

    public List<OrderSummaryView> getOrdersByStatus(OrderStatus status) {
        return orderViewRepository.findSummariesByStatus(status);
    }

    public List<OrderSummaryView> getOrdersByDateRange(LocalDateTime from, LocalDateTime to) {
        return orderViewRepository.findSummariesByDateRange(from, to);
    }

    public List<OrderSummaryView> getRecentOrders(int limit) {
        return orderViewRepository.findRecentOrderSummaries(limit);
    }

    // Analytics queries
    public OrderAnalytics getOrderAnalytics(String customerId) {
        return orderViewRepository.getOrderAnalytics(customerId);
    }

    public static class OrderAnalytics {
        private final int totalOrders;
        private final java.math.BigDecimal totalSpent;
        private final java.math.BigDecimal averageOrderValue;
        private final OrderStatus mostCommonStatus;

        public OrderAnalytics(int totalOrders, java.math.BigDecimal totalSpent,
                            java.math.BigDecimal averageOrderValue, OrderStatus mostCommonStatus) {
            this.totalOrders = totalOrders;
            this.totalSpent = totalSpent;
            this.averageOrderValue = averageOrderValue;
            this.mostCommonStatus = mostCommonStatus;
        }

        public int getTotalOrders() { return totalOrders; }
        public java.math.BigDecimal getTotalSpent() { return totalSpent; }
        public java.math.BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public OrderStatus getMostCommonStatus() { return mostCommonStatus; }
    }
}