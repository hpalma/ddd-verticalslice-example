package com.example.products.application;

import com.example.orders.shared.domain.events.OrderStockReductionFailed;
import com.example.shared.domain.DomainEventPublisher;
import com.example.shared.domain.StockReductionFailedException;
import com.example.shared.integration.OrderPlacedIntegrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProductOrderEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProductOrderEventHandler.class);
    
    private final ProductStockService stockService;
    private final DomainEventPublisher eventPublisher;

    public ProductOrderEventHandler(ProductStockService stockService, DomainEventPublisher eventPublisher) {
        this.stockService = stockService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void handleOrderPlaced(OrderPlacedIntegrationEvent event) {
        logger.info("Processing stock reduction for order: {}", event.getOrderId());
        
        try {
            for (OrderPlacedIntegrationEvent.ProductQuantityData item : event.getProductsToReduce()) {
                try {
                    stockService.reduceStock(item.getProductId(), item.getQuantity());
                } catch (StockReductionFailedException e) {
                    // Note: OrderStockReductionFailed still has a cross-context dependency
                    // In a real system, this should also be an integration event
                    eventPublisher.publishEvent(new OrderStockReductionFailed(
                        com.example.orders.shared.domain.OrderId.of(event.getOrderId()), 
                        e.getProductId(), 
                        e.getRequestedQuantity(), 
                        e.getAvailableQuantity(), 
                        e.getMessage()
                    ));
                    throw e;
                }
            }
            
            logger.info("Successfully reduced stock for all items in order: {}", 
                       event.getOrderId());
                       
        } catch (Exception e) {
            logger.error("Failed to reduce stock for order: {}. Error: {}", 
                        event.getOrderId(), e.getMessage());
            throw e;
        }
    }
}