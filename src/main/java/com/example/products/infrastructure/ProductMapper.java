package com.example.products.infrastructure;

import com.example.products.domain.Product;
import com.example.products.domain.ProductId;
import com.example.products.domain.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "price", target = "price", qualifiedByName = "priceToDecimal")
    ProductEntity toEntity(Product product);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToProductId")
    @Mapping(source = "price", target = "price", qualifiedByName = "decimalToPrice")
    Product toDomainBase(ProductEntity productEntity);

    @Named("stringToProductId")
    default ProductId stringToProductId(String id) {
        return ProductId.of(id);
    }

    @Named("priceToDecimal")
    default BigDecimal priceToDecimal(Price price) {
        return price.getAmount();
    }

    @Named("decimalToPrice")
    default Price decimalToPrice(BigDecimal amount) {
        return Price.of(amount, "USD");
    }

    default Product toDomain(ProductEntity productEntity) {
        Product product = toDomainBase(productEntity);
        if (!productEntity.getActive()) {
            product.deactivate();
        }
        return product;
    }
}