package com.example.products.application;

import com.example.orders.shared.domain.OrderId;
import com.example.orders.shared.domain.events.OrderStockReductionFailed;
import com.example.shared.domain.DomainEventPublisher;
import com.example.shared.domain.StockReductionFailedException;
import com.example.shared.integration.OrderPlacedIntegrationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOrderEventHandlerTest {

    @Mock
    private ProductStockService stockService;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    private ProductOrderEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new ProductOrderEventHandler(stockService, eventPublisher);
    }

    @Test
    void handleOrderPlaced_shouldReduceStockForAllItems() {
        String orderId = "order-123";
        List<OrderPlacedIntegrationEvent.ProductQuantityData> productsToReduce = List.of(
            new OrderPlacedIntegrationEvent.ProductQuantityData("prod-1", 2),
            new OrderPlacedIntegrationEvent.ProductQuantityData("prod-2", 1)
        );
        OrderPlacedIntegrationEvent event = new OrderPlacedIntegrationEvent(orderId, "customer-1", productsToReduce);

        eventHandler.handleOrderPlaced(event);

        verify(stockService).reduceStock("prod-1", 2);
        verify(stockService).reduceStock("prod-2", 1);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void handleOrderPlaced_shouldPublishFailureEventWhenStockInsufficient() {
        String orderId = "order-123";
        List<OrderPlacedIntegrationEvent.ProductQuantityData> productsToReduce = List.of(
            new OrderPlacedIntegrationEvent.ProductQuantityData("prod-1", 5)
        );
        OrderPlacedIntegrationEvent event = new OrderPlacedIntegrationEvent(orderId, "customer-1", productsToReduce);

        StockReductionFailedException exception = new StockReductionFailedException("prod-1", 5, 2);
        doThrow(exception).when(stockService).reduceStock("prod-1", 5);

        assertThrows(StockReductionFailedException.class, 
                    () -> eventHandler.handleOrderPlaced(event));

        ArgumentCaptor<OrderStockReductionFailed> eventCaptor = 
            ArgumentCaptor.forClass(OrderStockReductionFailed.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        OrderStockReductionFailed failureEvent = eventCaptor.getValue();
        assertEquals(orderId, failureEvent.getOrderId().getValue());
        assertEquals("prod-1", failureEvent.getProductId());
        assertEquals(5, failureEvent.getRequestedQuantity());
        assertEquals(2, failureEvent.getAvailableQuantity());
    }
}