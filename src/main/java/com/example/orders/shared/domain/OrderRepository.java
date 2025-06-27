package com.example.orders.shared.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(String customerId);
    List<Order> findAll();
}