# DDD Vertical Slice Architecture Demo with CQRS

This Spring Boot application demonstrates Domain-Driven Design (DDD) concepts implemented with a vertical slice architecture, featuring CQRS pattern, MapStruct mapping, and event-driven communication.

## Architecture Overview

### DDD Concepts Implemented

1. **Entities** - Objects with identity (Order, Product, OrderItem, BankAccount, Transaction)
2. **Value Objects** - Immutable objects without identity (OrderId, ProductId, Money, Price, AccountId, TransactionId)
3. **Aggregate Roots** - Consistency boundaries with business rule validation (Order, Product, BankAccount)
4. **Domain Events** - Events that occur within the domain (OrderCreated, AccountOpened, DepositMade, etc.)
5. **Integration Events** - Cross-bounded-context communication (OrderPlacedIntegrationEvent)
6. **Repositories** - Domain interface for data access
7. **Domain Services** - Business logic that doesn't belong to a single entity
8. **Bounded Contexts** - Clear boundaries between subdomains (Orders, Products, Banking)

### CQRS Pattern

The application implements Command Query Responsibility Segregation (CQRS) with:
- **Command Side**: Optimized for write operations, business logic execution, and consistency
- **Query Side**: Optimized for read operations, projections, and analytics
- **Shared Infrastructure**: Same datastore for both sides with different access patterns

### Event-Driven Architecture

- **Spring ApplicationEventPublisher**: For publishing domain and integration events
- **Cross-Context Communication**: Products slice automatically reduces stock when orders are placed
- **Event Handlers**: Asynchronous processing of domain events

### Object Mapping with MapStruct

- **Compile-time mapping**: Between domain objects and JPA entities
- **Type-safe conversions**: Automatic generation of mapping code
- **Custom mappings**: For complex value object transformations

## Project Structure

```
src/main/java/com/example/
├── shared/                    # Shared infrastructure and contracts
│   ├── domain/               # Base DDD building blocks
│   │   ├── Entity.java
│   │   ├── ValueObject.java
│   │   ├── AggregateRoot.java
│   │   ├── DomainEvent.java
│   │   └── DomainEventPublisher.java
│   └── integration/          # Cross-context integration events
│       └── OrderPlacedIntegrationEvent.java
├── orders/                   # Order management bounded context
│   ├── write/               # Command side (CQRS)
│   │   ├── api/            # Command controllers and DTOs
│   │   └── application/    # Command services and handlers
│   ├── read/                # Query side (CQRS)
│   │   ├── api/            # Query controllers
│   │   ├── application/    # Query services and view repositories
│   │   ├── infrastructure/ # Read-optimized data access
│   │   └── query/          # Read models and projections
│   └── shared/              # Shared between read/write sides
│       ├── domain/         # Order domain model and events
│       └── infrastructure/ # JPA entities, repositories, MapStruct mappers
├── products/                # Product catalog bounded context
│   ├── api/                # REST endpoints and DTOs
│   ├── application/        # Use cases and event handlers
│   ├── domain/             # Product domain model
│   └── infrastructure/     # Data persistence and MapStruct mappers
└── banking/                 # Banking bounded context (example)
    ├── application/        # Banking services and event handlers
    ├── domain/             # Account and transaction domain
    └── infrastructure/     # Banking data persistence
```

## Key Features

### 1. CQRS Implementation
- **Write Operations**: Handled by command services optimized for consistency and business logic
- **Read Operations**: Handled by query services with optimized projections and analytics
- **Separate Controllers**: OrderCommandController and OrderQueryController
- **Read Models**: OrderView, OrderSummaryView for optimized queries

### 2. Cross-Context Communication
- **Event-Driven Stock Management**: When orders are created, product stock is automatically reduced
- **Integration Events**: Proper bounded context boundaries with shared contracts
- **Error Handling**: Insufficient stock scenarios with compensating actions

### 3. MapStruct Object Mapping
- **Automatic Mapping**: Between Order ↔ OrderEntity, Product ↔ ProductEntity
- **Value Object Conversion**: Custom mappers for complex types like TransactionId, Money
- **Compile-time Safety**: No runtime reflection overhead

### 4. Domain Event System
- **Internal Events**: OrderCreated for within-context processing
- **Integration Events**: OrderPlacedIntegrationEvent for cross-context communication
- **Spring Events**: Leverages Spring's ApplicationEventPublisher infrastructure

### 5. Banking Example (Aggregate Root Showcase)
- **Business Rule Validation**: Daily withdrawal limits, account status checks
- **Version Control**: Optimistic locking for concurrent access
- **Complex Domain Logic**: Multiple transaction types with balance management

## Running the Application

### Prerequisites
- Java 17+ (tested with Java 24)
- Maven 3.9+ (Maven wrapper included)

### Build and Run
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or with system Maven
mvn spring-boot:run
```

The application will start on port 8080.

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=OrderCommandServiceTest
```

Note: Tests are configured to work with Java 24 using experimental Byte Buddy support.

## API Endpoints

### Products
- `POST /api/products` - Create a new product
- `GET /api/products` - Get all products  
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/active` - Get active products only
- `PUT /api/products/{id}/price` - Update product price
- `PUT /api/products/{id}/stock` - Update product stock
- `POST /api/products/{id}/deactivate` - Deactivate product

### Orders - Command Side (Write Operations)
- `POST /api/orders/commands` - Create a new order
- `POST /api/orders/commands/{id}/confirm` - Confirm order
- `POST /api/orders/commands/{id}/cancel` - Cancel order

### Orders - Query Side (Read Operations)  
- `GET /api/orders/queries` - Get all orders
- `GET /api/orders/queries/{id}` - Get order by ID
- `GET /api/orders/queries/customer/{customerId}` - Get orders by customer
- `GET /api/orders/queries/status/{status}` - Get orders by status
- `GET /api/orders/queries/recent?limit=10` - Get recent orders
- `GET /api/orders/queries/analytics/{customerId}` - Get customer analytics

## Example Usage

### 1. Create a Product
```json
POST /api/products
{
  "name": "Laptop",
  "description": "High-performance laptop", 
  "price": 999.99,
  "stockQuantity": 10
}
```

### 2. Create an Order (CQRS Command)
```json
POST /api/orders/commands
{
  "customerId": "customer-123",
  "items": [
    {
      "productId": "product-id",
      "productName": "Laptop",
      "unitPrice": 999.99,
      "quantity": 2
    }
  ]
}
```

### 3. Query Orders (CQRS Query)
```bash
# Get customer analytics
GET /api/orders/queries/analytics/customer-123

# Get recent orders
GET /api/orders/queries/recent?limit=5

# Get orders by status
GET /api/orders/queries/status/PENDING
```

## Event Flow Example

When an order is created:

1. **OrderCommandService** creates Order aggregate
2. **OrderCreated** domain event is published (internal to orders context)
3. **OrderPlacedIntegrationEvent** is published (cross-context)
4. **ProductOrderEventHandler** catches integration event
5. **ProductStockService** reduces stock for each ordered item
6. If insufficient stock: **OrderStockReductionFailed** event is published

## Database

The application uses H2 in-memory database for simplicity. Access the H2 console at:
http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa` 
- Password: (empty)

### Database Schema

The application creates tables for:
- **orders** and **order_items** - Order management
- **products** - Product catalog
- **bank_accounts** and **transactions** - Banking example
- Proper foreign key relationships and constraints

## Technical Implementation Details

### MapStruct Configuration
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

### Java 24 Compatibility
The project includes configuration for Java 24 compatibility:
- Maven Surefire plugin with dynamic agent loading
- Experimental Byte Buddy support for Mockito
- Proper module system configuration

### Testing Strategy
- **Unit Tests**: Domain logic and service layer testing
- **Integration Tests**: Cross-context communication testing  
- **Mockito**: Service mocking with Java 24 compatibility
- **Spring Boot Test**: Full application context testing

## Key Design Decisions

1. **CQRS with Single Datastore**: Simplified deployment while maintaining read/write separation
2. **Shared Package Structure**: Clear separation of shared components in CQRS contexts
3. **Integration Events**: Proper bounded context boundaries instead of direct dependencies
4. **MapStruct Over Manual Mapping**: Compile-time safety and performance
5. **Spring Events**: Leveraging framework capabilities for event-driven architecture
6. **Value Objects**: Immutable objects for type safety and domain expressiveness

This implementation showcases modern DDD practices with practical Spring Boot integration, demonstrating how to build maintainable, scalable applications with clear architectural boundaries.