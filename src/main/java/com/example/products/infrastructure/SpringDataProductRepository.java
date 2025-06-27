package com.example.products.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    List<ProductEntity> findByActiveTrue();
}