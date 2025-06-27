package com.example.products.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product);
    Optional<Product> findById(ProductId id);
    List<Product> findAll();
    List<Product> findByNameContaining(String name);
    List<Product> findActiveProducts();
}