package com.example.integration;

import com.example.orders.write.api.CreateOrderRequest;
import com.example.orders.write.application.OrderCommandService;
import com.example.orders.shared.domain.OrderId;
import com.example.products.api.CreateProductRequest;
import com.example.products.application.ProductApplicationService;
import com.example.products.domain.ProductId;
import com.example.products.domain.ProductRepository;
import com.example.shared.domain.StockReductionFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderProductIntegrationTest {

    @Autowired
    private OrderCommandService orderService;
    
    @Autowired
    private ProductApplicationService productService;
    
    @Autowired
    private ProductRepository productRepository;

    @Test
    void createOrder_shouldReduceProductStock() {
        ProductId productId = productService.createProduct(
            new CreateProductRequest("Test Product", "Description", BigDecimal.valueOf(10.00), 5)
        );

        CreateOrderRequest.OrderItemRequest orderItem = 
            new CreateOrderRequest.OrderItemRequest(productId.getValue(), "Test Product", BigDecimal.valueOf(10.00), 2);
        CreateOrderRequest orderRequest = new CreateOrderRequest("customer-1", List.of(orderItem));

        OrderId orderId = orderService.createOrder(orderRequest);

        assertNotNull(orderId);

        var product = productRepository.findById(productId).orElseThrow();
        assertEquals(3, product.getStockQuantity(), "Stock should be reduced from 5 to 3");
    }

    @Test
    void createOrder_shouldFailWhenInsufficientStock() {
        ProductId productId = productService.createProduct(
            new CreateProductRequest("Test Product", "Description", BigDecimal.valueOf(10.00), 2)
        );

        CreateOrderRequest.OrderItemRequest orderItem = 
            new CreateOrderRequest.OrderItemRequest(productId.getValue(), "Test Product", BigDecimal.valueOf(10.00), 5);
        CreateOrderRequest orderRequest = new CreateOrderRequest("customer-1", List.of(orderItem));

        assertThrows(StockReductionFailedException.class, 
                    () -> orderService.createOrder(orderRequest));

        var product = productRepository.findById(productId).orElseThrow();
        assertEquals(2, product.getStockQuantity(), "Stock should remain unchanged when order fails");
    }
}