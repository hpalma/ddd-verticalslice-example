package com.example.orders.shared.infrastructure;

import com.example.orders.shared.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, String> {
    // Write-side queries (simple lookups)
    List<OrderEntity> findByCustomerId(String customerId);
    
    // Read-side optimized queries
    List<OrderEntity> findByStatus(OrderStatus status);
    List<OrderEntity> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    
    @Query("SELECT o FROM OrderEntity o ORDER BY o.createdAt DESC LIMIT ?1")
    List<OrderEntity> findTopByOrderByCreatedAtDesc(int limit);
}