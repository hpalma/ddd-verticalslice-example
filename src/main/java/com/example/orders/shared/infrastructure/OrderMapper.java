package com.example.orders.shared.infrastructure;

import com.example.orders.shared.domain.Order;
import com.example.orders.shared.domain.OrderId;
import com.example.orders.shared.domain.OrderItem;
import com.example.orders.shared.domain.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "items", target = "items", qualifiedByName = "orderItemsToEntities")
    OrderEntity toEntity(Order order);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToOrderId")
    @Mapping(source = "items", target = "items", qualifiedByName = "orderItemEntitiesToDomain")
    Order toDomain(OrderEntity orderEntity);

    @Named("stringToOrderId")
    default OrderId stringToOrderId(String id) {
        return OrderId.of(id);
    }

    @Named("orderItemsToEntities")
    default List<OrderItemEntity> orderItemsToEntities(List<OrderItem> items) {
        return items.stream()
                .map(this::orderItemToEntity)
                .toList();
    }

    @Named("orderItemEntitiesToDomain")
    default List<OrderItem> orderItemEntitiesToDomain(List<OrderItemEntity> items) {
        return items.stream()
                .map(this::orderItemEntityToDomain)
                .toList();
    }

    @Mapping(target = "order", ignore = true)
    OrderItemEntity orderItemToEntity(OrderItem orderItem);

    @Mapping(source = "unitPrice", target = "unitPrice", qualifiedByName = "bigDecimalToMoney")
    OrderItem orderItemEntityToDomain(OrderItemEntity orderItemEntity);

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal amount) {
        return Money.of(amount, "USD");
    }

    default BigDecimal moneyToBigDecimal(Money money) {
        return money.getAmount();
    }
}