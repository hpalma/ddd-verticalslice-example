package com.example.products.api;

import com.example.products.application.ProductApplicationService;
import com.example.products.domain.Product;
import com.example.products.domain.ProductId;
import com.example.products.domain.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductApplicationService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductApplicationService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        ProductId productId = productService.createProduct(request);
        Product product = productRepository.findById(productId).orElseThrow();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.fromDomain(product));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        return productRepository.findById(ProductId.of(productId))
                .map(ProductResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @GetMapping("/active")
    public List<ProductResponse> getActiveProducts() {
        return productService.findActiveProducts().stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @PutMapping("/{productId}/price")
    public ResponseEntity<Void> updatePrice(@PathVariable String productId, 
                                          @RequestBody UpdatePriceRequest request) {
        productService.updateProductPrice(ProductId.of(productId), request.price());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable String productId, 
                                          @RequestBody UpdateStockRequest request) {
        productService.updateProductStock(ProductId.of(productId), request.quantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable String productId) {
        productService.deactivateProduct(ProductId.of(productId));
        return ResponseEntity.ok().build();
    }
}