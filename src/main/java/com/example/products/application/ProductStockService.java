package com.example.products.application;

import com.example.products.domain.Product;
import com.example.products.domain.ProductId;
import com.example.products.domain.ProductRepository;
import com.example.shared.domain.StockReductionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductStockService {
    private static final Logger logger = LoggerFactory.getLogger(ProductStockService.class);
    
    private final ProductRepository productRepository;

    public ProductStockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void reduceStock(String productId, int quantity) {
        ProductId id = ProductId.of(productId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity) {
            logger.warn("Insufficient stock for product {}. Available: {}, Requested: {}", 
                       productId, product.getStockQuantity(), quantity);
            throw new StockReductionFailedException(productId, quantity, product.getStockQuantity());
        }

        int newStock = product.getStockQuantity() - quantity;
        product.updateStock(newStock);
        productRepository.save(product);
        
        logger.info("Reduced stock for product {} by {}. New stock level: {}", 
                   productId, quantity, newStock);
    }
}