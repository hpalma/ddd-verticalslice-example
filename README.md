# DDD Vertical Slice Architecture Demo

This Spring Boot application demonstrates Domain-Driven Design (DDD) concepts implemented with a vertical slice architecture.

## Architecture Overview

### DDD Concepts Implemented

1. **Entities** - Objects with identity (Order, Product, OrderItem)
2. **Value Objects** - Immutable objects without identity (OrderId, ProductId, Money, Price)
3. **Aggregate Roots** - Consistency boundaries (Order, Product)
4. **Domain Events** - Events that occur within the domain (OrderCreated)
5. **Repositories** - Domain interface for data access
6. **Domain Services** - Business logic that doesn't belong to a single entity

### Vertical Slice Architecture

Each feature is organized in vertical slices containing:
- **Domain Layer** - Core business logic, entities, value objects
- **Application Layer** - Use cases, command handlers, application services
- **Infrastructure Layer** - Data persistence, external integrations
- **API Layer** - REST controllers, DTOs, request/response objects

## Project Structure

```
src/main/java/com/example/
├── shared/domain/          # Base DDD building blocks
│   ├── Entity.java
│   ├── ValueObject.java
│   ├── AggregateRoot.java
│   └── DomainEvent.java
├── orders/                 # Order management vertical slice
│   ├── domain/            # Order domain model
│   ├── application/       # Order use cases
│   ├── infrastructure/    # Order data persistence
│   └── api/              # Order REST endpoints
└── products/              # Product catalog vertical slice
    ├── domain/           # Product domain model
    ├── application/      # Product use cases
    ├── infrastructure/   # Product data persistence
    └── api/             # Product REST endpoints
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

### Products
- `POST /api/products` - Create a new product
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/active` - Get active products only
- `PUT /api/products/{id}/price` - Update product price
- `PUT /api/products/{id}/stock` - Update product stock
- `POST /api/products/{id}/deactivate` - Deactivate product

### Orders
- `POST /api/orders` - Create a new order
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders/{id}/confirm` - Confirm order
- `POST /api/orders/{id}/cancel` - Cancel order

## Example Usage

1. Create a product:
```json
POST /api/products
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 10
}
```

2. Create an order:
```json
POST /api/orders
{
  "customerId": "customer-123",
  "items": [
    {
      "productId": "product-id",
      "productName": "Laptop",
      "unitPrice": 999.99,
      "quantity": 1
    }
  ]
}
```

## Database

The application uses H2 in-memory database for simplicity. You can access the H2 console at:
http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)