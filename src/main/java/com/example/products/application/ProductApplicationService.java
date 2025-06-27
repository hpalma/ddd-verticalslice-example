package com.example.products.application;

import com.example.products.api.CreateProductRequest;
import com.example.products.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductApplicationService {
    private final ProductRepository productRepository;

    public ProductApplicationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductId createProduct(CreateProductRequest request) {
        ProductId productId = ProductId.generate();
        Price price = Price.of(request.price(), "USD");
        
        Product product = new Product(
                productId,
                request.name(),
                request.description(),
                price,
                request.stockQuantity()
        );
        
        productRepository.save(product);
        return productId;
    }

    public void updateProductPrice(ProductId productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        product.updatePrice(Price.of(newPrice, "USD"));
        productRepository.save(product);
    }

    public void updateProductStock(ProductId productId, int newQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        product.updateStock(newQuantity);
        productRepository.save(product);
    }

    public void deactivateProduct(ProductId productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        product.deactivate();
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> findActiveProducts() {
        return productRepository.findActiveProducts();
    }
}