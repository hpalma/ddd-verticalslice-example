package com.example.orders.read.infrastructure;

import com.example.orders.shared.domain.OrderStatus;
import com.example.orders.shared.infrastructure.OrderEntity;
import com.example.orders.shared.infrastructure.SpringDataOrderRepository;
import com.example.orders.read.application.OrderQueryService;
import com.example.orders.read.application.OrderViewRepository;
import com.example.orders.read.query.OrderSummaryView;
import com.example.orders.read.query.OrderView;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Read repository implementation using the same JPA entities as write side,
 * but with read-optimized queries and projections.
 */
@Repository
public class JpaOrderViewRepository implements OrderViewRepository {
    private final SpringDataOrderRepository springDataRepository;

    public JpaOrderViewRepository(SpringDataOrderRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<OrderView> findByOrderId(String orderId) {
        return springDataRepository.findById(orderId)
                .map(this::toOrderView);
    }

    @Override
    public List<OrderSummaryView> findSummariesByCustomerId(String customerId) {
        return springDataRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toOrderSummaryView)
                .toList();
    }

    @Override
    public List<OrderSummaryView> findSummariesByStatus(OrderStatus status) {
        return springDataRepository.findByStatus(status)
                .stream()
                .map(this::toOrderSummaryView)
                .toList();
    }

    @Override
    public List<OrderSummaryView> findSummariesByDateRange(LocalDateTime from, LocalDateTime to) {
        return springDataRepository.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toOrderSummaryView)
                .toList();
    }

    @Override
    public List<OrderSummaryView> findRecentOrderSummaries(int limit) {
        return springDataRepository.findTopByOrderByCreatedAtDesc(limit)
                .stream()
                .map(this::toOrderSummaryView)
                .toList();
    }

    @Override
    public OrderQueryService.OrderAnalytics getOrderAnalytics(String customerId) {
        List<OrderEntity> customerOrders = springDataRepository.findByCustomerId(customerId);
        
        int totalOrders = customerOrders.size();
        BigDecimal totalSpent = customerOrders.stream()
                .map(this::calculateOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = totalOrders > 0 
                ? totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        OrderStatus mostCommonStatus = customerOrders.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    OrderEntity::getStatus,
                    java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(OrderStatus.PENDING);

        return new OrderQueryService.OrderAnalytics(
                totalOrders, totalSpent, averageOrderValue, mostCommonStatus);
    }

    private OrderView toOrderView(OrderEntity entity) {
        List<OrderView.OrderItemView> itemViews = entity.getItems().stream()
                .map(item -> new OrderView.OrderItemView(
                        item.getProductId(),
                        item.getProductName(),
                        "Unknown Category", // In real app, fetch from product context or cache
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        BigDecimal totalAmount = calculateOrderTotal(entity);

        return new OrderView(
                entity.getId(),
                entity.getCustomerId(),
                "Unknown Customer", // In real app, fetch from customer context or cache
                entity.getCreatedAt(),
                entity.getStatus(),
                totalAmount,
                "USD",
                entity.getItems().size(),
                itemViews
        );
    }

    private OrderSummaryView toOrderSummaryView(OrderEntity entity) {
        BigDecimal totalAmount = calculateOrderTotal(entity);

        return new OrderSummaryView(
                entity.getId(),
                entity.getCustomerId(),
                "Unknown Customer", // In real app, fetch from customer context or cache
                entity.getCreatedAt(),
                entity.getStatus(),
                totalAmount,
                entity.getItems().size()
        );
    }

    private BigDecimal calculateOrderTotal(OrderEntity entity) {
        return entity.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}