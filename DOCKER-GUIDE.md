# Docker Compose Guide

## Quick Start

### 1. Build all Docker images

```bash
mvn clean compile jib:dockerBuild
```

### 2. Start all services

```bash
docker-compose up -d
```

### 3. Check service status

```bash
docker-compose ps
```

### 4. View logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f order-service
```

### 5. Stop all services

```bash
docker-compose down
```

### 6. Stop and remove volumes (clean slate)

```bash
docker-compose down -v
```

## Service Access URLs

| Service           | URL                   | Notes                                |
| ----------------- | --------------------- | ------------------------------------ |
| API Gateway       | http://localhost:8080 | Main entry point                     |
| Discovery Server  | http://localhost:8761 | Username: eureka, Password: password |
| Product Service   | http://localhost:8081 | Via Gateway: /api/product            |
| Order Service     | http://localhost:8082 | Via Gateway: /api/order              |
| Inventory Service | http://localhost:8083 | Via Gateway: /api/inventory          |
| Zipkin            | http://localhost:9411 | Distributed tracing UI               |
| Kafka UI          | N/A                   | Broker at localhost:9092             |

## Databases

| Database          | Port  | Credentials  |
| ----------------- | ----- | ------------ |
| MySQL (Order)     | 3306  | root/mysql   |
| MySQL (Inventory) | 3307  | root/mysql   |
| MongoDB (Product) | 27017 | root/mongodb |

## Testing the Application

### 1. Create a Product

```bash
curl -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "description": "Latest iPhone model",
    "price": 1299
  }'
```

### 2. Get all Products

```bash
curl http://localhost:8080/api/product
```

### 3. Check Inventory

```bash
curl "http://localhost:8080/api/inventory?skuCode=iphone_15"
```

### 4. Place an Order

```bash
curl -X POST http://localhost:8080/api/order \
  -H "Content-Type: application/json" \
  -d '{
    "orderLineItemsDtoList": [{
      "skuCode": "iphone_15",
      "price": 1299,
      "quantity": 1
    }]
  }'
```

## Troubleshooting

### Services not starting

```bash
# Check logs
docker-compose logs [service-name]

# Restart a specific service
docker-compose restart [service-name]
```

### Clean restart

```bash
# Stop everything and remove volumes
docker-compose down -v

# Remove old images
docker-compose down --rmi all

# Rebuild and start
mvn clean compile jib:dockerBuild
docker-compose up -d
```

### Check service health

```bash
# Discovery Server
curl http://localhost:8761/actuator/health

# Order Service (includes circuit breaker status)
curl http://localhost:8082/actuator/health
```

### Database connection issues

```bash
# Wait for databases to be ready (30-60 seconds)
docker-compose logs mysql-order
docker-compose logs mongodb
```

## Development Workflow

### Update a service

```bash
# 1. Make code changes

# 2. Rebuild that service's image
cd [service-directory]
mvn clean compile jib:dockerBuild

# 3. Restart the service
docker-compose restart [service-name]
```

### View real-time logs

```bash
docker-compose logs -f --tail=100 order-service
```

## Useful Commands

```bash
# Scale a service (if stateless)
docker-compose up -d --scale product-service=3

# Execute command in container
docker-compose exec order-service sh

# View resource usage
docker stats

# Prune unused Docker resources
docker system prune -a --volumes
```

## Architecture Flow

```
Client Request
    ↓
API Gateway (8080)
    ↓
Discovery Server (8761) - Service Registry
    ↓
Microservices (8081-8084)
    ↓
Databases (MySQL, MongoDB)
    ↓
Kafka (Events) → Notification Service
    ↓
Zipkin (Tracing)
```

## Notes

- Services use Spring profiles (`docker`) for containerized configuration
- All services automatically register with Eureka
- Kafka events are published when orders are placed
- All requests are traced through Zipkin
- Circuit breakers protect against service failures
- Data persists in Docker volumes
