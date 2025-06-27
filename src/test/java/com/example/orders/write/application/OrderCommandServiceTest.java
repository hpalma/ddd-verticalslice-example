package com.example.orders.write.application;

import com.example.orders.write.api.CreateOrderRequest;
import com.example.orders.shared.domain.*;
import com.example.orders.shared.domain.events.OrderCreated;
import com.example.shared.domain.DomainEvent;
import com.example.shared.domain.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    private OrderCommandService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderCommandService(orderRepository, eventPublisher);
    }

    @Test
    void createOrder_shouldSaveOrderAndPublishEvent() {
        CreateOrderRequest.OrderItemRequest itemRequest =
            new CreateOrderRequest.OrderItemRequest("prod-1", "Product 1", BigDecimal.valueOf(10.00), 2);
        CreateOrderRequest request = new CreateOrderRequest("customer-1", List.of(itemRequest));

        OrderId orderId = orderService.createOrder(request);

        assertNotNull(orderId);
        verify(orderRepository).save(any(Order.class));
        
        // Verify both domain event and integration event were published
        verify(eventPublisher, times(2)).publishEvent(any());
        
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent((DomainEvent) eventCaptor.capture());
        
        // Check that OrderCreated domain event was published
        boolean domainEventPublished = eventCaptor.getAllValues().stream()
                .anyMatch(event -> event instanceof OrderCreated);
        assertTrue(domainEventPublished);
        
        // Check that integration event was published  
        boolean integrationEventPublished = eventCaptor.getAllValues().stream()
                .anyMatch(event -> event instanceof com.example.shared.integration.OrderPlacedIntegrationEvent);
        assertTrue(integrationEventPublished);
    }
}