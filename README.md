# MicroServicesJava

A comprehensive Spring Boot microservices architecture project demonstrating service discovery, API gateway, distributed tracing, circuit breakers, event-driven architecture with Kafka, and observability with Zipkin.

## Architecture Overview

This project consists of multiple microservices with robust patterns:

### 🛍️ Product Service (Port: 8081)

- **Database**: MongoDB
- **Functionality**: Manages product catalog (create, retrieve products)
- **Technology**: Spring Boot, Spring Data MongoDB
- **Testing**: Integration tests with Testcontainers
- **Features**: Service registration with Eureka, distributed tracing

### 📦 Order Service (Port: 8082)

- **Database**: MySQL
- **Functionality**: Handles order placement and management
- **Technology**: Spring Boot, Spring Data JPA, WebClient for inter-service communication
- **Dependencies**: Communicates with Inventory Service for stock verification
- **Features**: Circuit breaker pattern with Resilience4j, Kafka event publishing, distributed tracing

### 📊 Inventory Service (Port: 8083)

- **Database**: MySQL
- **Functionality**: Manages product inventory and stock levels
- **Technology**: Spring Boot, Spring Data JPA
- **Features**: Stock verification API for multiple products, service registration

### � Notification Service (Dynamic Port)

- **Functionality**: Handles order notifications via Kafka
- **Technology**: Spring Boot, Kafka Consumer
- **Features**: Event-driven architecture, distributed tracing

### �🔍 Discovery Server (Port: 8761)

- **Technology**: Spring Cloud Netflix Eureka
- **Functionality**: Service registry and discovery
- **Security**: Basic authentication (username: `eureka`, password: `password`)
- **URL**: http://localhost:8761

### 🚪 API Gateway (Port: 8080)

- **Technology**: Spring Cloud Gateway
- **Functionality**: Single entry point for all microservices, load balancing, routing
- **Features**: Service discovery integration, security configuration, distributed tracing
- **Routes**:
  - `/api/product/**` → Product Service
  - `/api/order/**` → Order Service
  - `/api/inventory/**` → Inventory Service

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Java Version**: 17
- **Build Tool**: Maven
- **Databases**:
  - MongoDB (Product Service)
  - MySQL (Order & Inventory Services)
- **Message Broker**: Apache Kafka with Zookeeper
- **Service Discovery**: Eureka Server
- **API Gateway**: Spring Cloud Gateway
- **Distributed Tracing**: Zipkin with Micrometer Tracing
- **Circuit Breaker**: Resilience4j
- **Testing**: JUnit 5, Testcontainers
- **Service Communication**: WebClient (Reactive)

## Project Structure

```
microservices-new/
├── product-service/          # Product management service
├── order-service/            # Order processing service
├── inventory-service/        # Inventory management service
├── notification-service/     # Notification service (Kafka consumer)
├── discovery-server/         # Eureka service registry
├── api-gateway/             # API Gateway for routing
├── docker-compose.yml       # Docker setup for Kafka, Zookeeper, Zipkin
├── pom.xml                  # Parent POM
└── README.md
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB (for Product Service)
- MySQL 8.0+ (for Order & Inventory Services)
- Docker & Docker Compose (for Kafka, Zookeeper, and Zipkin)

### Infrastructure Setup with Docker

Start the required infrastructure services (Kafka, Zookeeper, and Zipkin):

```bash
docker-compose up -d
```

This will start:

- **Zookeeper** on port 2181
- **Kafka Broker** on port 9092
- **Zipkin** on port 9411

Access Zipkin UI: http://localhost:9411

### Database Setup

#### MySQL Setup

```sql
CREATE DATABASE `order-service`;
CREATE DATABASE `inventory-service`;
```

#### MongoDB Setup

- Default connection: `mongodb://localhost:27017/product-service`

### Running the Services

**Recommended Startup Order:**

1. **Start Infrastructure (Docker)**

```bash
docker-compose up -d
```

2. **Start Discovery Server**

```bash
cd discovery-server
mvn spring-boot:run
```

Wait for it to be fully started, then access: http://localhost:8761

3. **Start API Gateway**

```bash
cd api-gateway
mvn spring-boot:run
```

4. **Start Product Service**

```bash
cd product-service
mvn spring-boot:run
```

5. **Start Inventory Service**

```bash
cd inventory-service
mvn spring-boot:run
```

6. **Start Order Service**

```bash
cd order-service
mvn spring-boot:run
```

7. **Start Notification Service**

```bash
cd notification-service
mvn spring-boot:run
```

### API Endpoints

**Note**: All services can be accessed through the API Gateway at `http://localhost:8080`

#### API Gateway (http://localhost:8080)

- Main entry point for all services
- Routes requests to appropriate microservices

#### Product Service (Direct: 8081, Via Gateway: 8080)

- `POST /api/product` - Create a new product
- `GET /api/product` - Get all products

#### Inventory Service (Direct: 8083, Via Gateway: 8080)

- `GET /api/inventory?skuCode=product1&skuCode=product2` - Check stock for products

#### Order Service (Direct: 8082, Via Gateway: 8080)

- `POST /api/order` - Place a new order
- `GET /actuator/health` - Health check with circuit breaker status

#### Discovery Server (http://localhost:8761)

- Eureka Dashboard - View all registered services
- Login: `eureka` / `password`

### Sample API Calls

**Note**: Use API Gateway (port 8080) as the entry point for all requests.

#### Create Product

```bash
curl -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 13",
    "description": "Latest iPhone model",
    "price": 1200
  }'
```

#### Place Order (via API Gateway)

```bash
curl -X POST http://localhost:8080/api/order \
  -H "Content-Type: application/json" \
  -d '{
    "orderLineItemsDtoList": [{
      "skuCode": "iphone_13",
      "price": 1200,
      "quantity": 1
    }]
  }'
```

#### Check Inventory

```bash
curl "http://localhost:8080/api/inventory?skuCode=iphone_13"
```

#### Check Circuit Breaker Health

```bash
curl http://localhost:8082/actuator/health
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

- ✅ **Microservices Architecture** - Multiple independent services
- ✅ **Service Discovery** - Netflix Eureka for dynamic service registration
- ✅ **API Gateway** - Spring Cloud Gateway with routing and load balancing
- ✅ **Distributed Tracing** - Zipkin integration for request tracing across services
- ✅ **Circuit Breaker Pattern** - Resilience4j for fault tolerance
- ✅ **Event-Driven Architecture** - Kafka for asynchronous messaging
- ✅ **Service-to-Service Communication** - WebClient (Reactive)
- ✅ **Multiple Database Integration** - MongoDB + MySQL
- ✅ **Integration Testing** - Testcontainers for realistic testing
- ✅ **Health Checks & Monitoring** - Spring Boot Actuator
- ✅ **Security** - Basic authentication on Discovery Server
- ✅ **Proper Error Handling** - Comprehensive error handling and fallback mechanisms
- ✅ **Transaction Management** - Distributed transaction handling
- ✅ **Containerization** - Docker Compose for infrastructure services

## Monitoring & Observability

### Zipkin Tracing

- **URL**: http://localhost:9411
- **Purpose**: View distributed traces across all microservices
- **Configuration**: 100% sampling rate for development

### Eureka Dashboard

- **URL**: http://localhost:8761
- **Credentials**: `eureka` / `password`
- **Purpose**: Monitor service registration and health

### Circuit Breaker Monitoring

- **URL**: http://localhost:8082/actuator/health
- **Purpose**: Check circuit breaker status and inventory service health

## Key Patterns & Practices

1. **Database per Service**: Each microservice has its own database
2. **API Gateway Pattern**: Single entry point for all client requests
3. **Service Registry Pattern**: Dynamic service discovery with Eureka
4. **Circuit Breaker Pattern**: Resilience4j for handling service failures
5. **Event-Driven Architecture**: Kafka for asynchronous event processing
6. **Distributed Tracing**: Request correlation across services with Zipkin
7. **Health Check Pattern**: Actuator endpoints for monitoring
8. **Externalized Configuration**: Properties files for service configuration

## Development Notes

- All services communicate via HTTP REST APIs through the API Gateway
- Order Service validates inventory before placing orders
- Circuit breaker protects Order Service from Inventory Service failures
- Kafka is used for asynchronous order notifications
- Each service has its own database (Database per Service pattern)
- All services register with Eureka for service discovery
- Distributed tracing helps monitor request flows across services
- Comprehensive error handling with fallback mechanisms

## Troubleshooting

### Services not registering with Eureka

- Ensure Discovery Server is running first
- Check Eureka credentials in application.properties
- Wait 30 seconds for service registration

### Circuit breaker not working

- Verify Resilience4j dependencies in pom.xml
- Check actuator health endpoint
- Ensure AOP is enabled

### Zipkin traces not appearing

- Confirm Zipkin is running: `docker ps`
- Check tracing configuration in application.properties
- Verify sampling probability is set to 1.0

### Kafka connection issues

- Ensure docker-compose services are running
- Check broker accessibility: `localhost:9092`
- Verify Kafka topic creation

## Stopping Services

```bash
# Stop all Docker containers
docker-compose down

# Stop Spring Boot services
# Use Ctrl+C in each terminal
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is open source and available under the [MIT License](LICENSE).
