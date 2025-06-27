package com.example.products.infrastructure;

import com.example.products.domain.Product;
import com.example.products.domain.ProductId;
import com.example.products.domain.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductRepository implements ProductRepository {
    private final SpringDataProductRepository springDataRepository;
    private final ProductMapper productMapper;

    public JpaProductRepository(SpringDataProductRepository springDataRepository, ProductMapper productMapper) {
        this.springDataRepository = springDataRepository;
        this.productMapper = productMapper;
    }

    @Override
    public void save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return springDataRepository.findById(id.getValue())
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return springDataRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findActiveProducts() {
        return springDataRepository.findByActiveTrue()
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }
}