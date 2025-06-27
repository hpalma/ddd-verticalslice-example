package com.example.orders.shared.infrastructure;

import com.example.orders.shared.domain.Order;
import com.example.orders.shared.domain.OrderId;
import com.example.orders.shared.domain.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataOrderRepository springDataRepository;
    private final OrderMapper orderMapper;

    public JpaOrderRepository(SpringDataOrderRepository springDataRepository, OrderMapper orderMapper) {
        this.springDataRepository = springDataRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public void save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        for (OrderItemEntity item : entity.getItems()) {
            item.setOrder(entity);
        }
        springDataRepository.save(entity);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return springDataRepository.findById(id.getValue())
                .map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return springDataRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }
}