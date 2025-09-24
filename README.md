# MicroServicesJava

A Spring Boot microservices architecture project demonstrating inter-service communication, database integration, and containerization.

## Architecture Overview

This project consists of three main microservices:

### 🛍️ Product Service (Port: 8081)

- **Database**: MongoDB
- **Functionality**: Manages product catalog (create, retrieve products)
- **Technology**: Spring Boot, Spring Data MongoDB
- **Testing**: Integration tests with Testcontainers

### 📦 Order Service (Port: 8082)

- **Database**: MySQL
- **Functionality**: Handles order placement and management
- **Technology**: Spring Boot, Spring Data JPA, WebClient for inter-service communication
- **Dependencies**: Communicates with Inventory Service for stock verification

### 📊 Inventory Service (Port: 8083)

- **Database**: MySQL
- **Functionality**: Manages product inventory and stock levels
- **Technology**: Spring Boot, Spring Data JPA
- **Features**: Stock verification API for multiple products

### 🔍 Discovery Server (Port: 8761)

- **Technology**: Spring Cloud Netflix Eureka
- **Functionality**: Service registry and discovery

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Java Version**: 17
- **Build Tool**: Maven
- **Databases**:
  - MongoDB (Product Service)
  - MySQL (Order & Inventory Services)
- **Testing**: JUnit 5, Testcontainers
- **Service Communication**: WebClient (Reactive)
- **Service Discovery**: Eureka Server

## Project Structure

```
microservices-new/
├── product-service/          # Product management service
├── order-service/            # Order processing service
├── inventory-service/        # Inventory management service
├── discovery-server/         # Eureka service registry
├── pom.xml                  # Parent POM
└── README.md
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB (for Product Service)
- MySQL 8.0+ (for Order & Inventory Services)
- Docker (optional, for database containers)

### Database Setup

#### MySQL Setup

```sql
CREATE DATABASE `order-service`;
CREATE DATABASE `inventory-service`;
```

#### MongoDB Setup

- Default connection: `mongodb://localhost:27017/product-service`

### Running the Services

1. **Start Discovery Server** (Optional but recommended)

```bash
cd discovery-server
mvn spring-boot:run
```

2. **Start Product Service**

```bash
cd product-service
mvn spring-boot:run
```

3. **Start Inventory Service**

```bash
cd inventory-service
mvn spring-boot:run
```

4. **Start Order Service**

```bash
cd order-service
mvn spring-boot:run
```

### API Endpoints

#### Product Service (http://localhost:8081)

- `POST /api/product` - Create a new product
- `GET /api/product` - Get all products

#### Inventory Service (http://localhost:8083)

- `GET /api/inventory?skuCode=product1&skuCode=product2` - Check stock for products

#### Order Service (http://localhost:8082)

- `POST /api/order` - Place a new order
- `GET /actuator/health` - Health check

### Sample API Calls

#### Create Product

```bash
curl -X POST http://localhost:8081/api/product \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 13",
    "description": "Latest iPhone model",
    "price": 1200
  }'
```

#### Place Order

```bash
curl -X POST http://localhost:8082/api/order \
  -H "Content-Type: application/json" \
  -d '{
    "orderLineItemsDtoList": [{
      "skuCode": "iphone 13",
      "price": 1200,
      "quantity": 1
    }]
  }'
```

#### Check Inventory

```bash
curl "http://localhost:8083/api/inventory?skuCode=iphone%2013"
```

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

The project includes comprehensive integration tests using Testcontainers for realistic database testing.

## Features Implemented

- ✅ Microservices Architecture
- ✅ Service-to-Service Communication (WebClient)
- ✅ Multiple Database Integration (MongoDB + MySQL)
- ✅ Integration Testing with Testcontainers
- ✅ Service Discovery Ready (Eureka)
- ✅ Health Checks & Monitoring
- ✅ Proper Error Handling
- ✅ Transaction Management

## Development Notes

- Services communicate via HTTP REST APIs
- Order Service validates inventory before placing orders
- Each service has its own database (Database per Service pattern)
- Comprehensive error handling and validation
- Ready for containerization with Docker

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is open source and available under the [MIT License](LICENSE).
