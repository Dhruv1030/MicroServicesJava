# 🚀 Quick Start Guide

## Prerequisites
- Docker & Docker Compose installed
- All Docker images built (see below)

## Step-by-Step Setup

### 1️⃣ Build Docker Images (First Time Only)
```bash
mvn clean compile jib:dockerBuild -DskipTests
```
⏱️ Takes ~20 seconds

### 2️⃣ Start Everything
```bash
docker-compose up -d
```
⏱️ Wait 30-60 seconds for all services to start

### 3️⃣ Check Status
```bash
docker-compose ps
```
All services should show "Up"

### 4️⃣ Access Services

| Service | URL | Credentials |
|---------|-----|-------------|
| 🌐 API Gateway | http://localhost:8080 | - |
| 🔍 Eureka Dashboard | http://localhost:8761 | eureka / password |
| 📊 Zipkin Tracing | http://localhost:9411 | - |

### 5️⃣ Test the Application

**Create a Product:**
```bash
curl -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/json" \
  -d '{"name": "iPhone 15", "description": "Latest iPhone", "price": 1299}'
```

**Get All Products:**
```bash
curl http://localhost:8080/api/product
```

**Place an Order:**
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

### 6️⃣ View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f order-service
```

### 7️⃣ Stop Everything
```bash
docker-compose down
```

## 🎉 That's It!
Your complete microservices architecture is running!

## 📚 More Information
- See `DOCKER-GUIDE.md` for detailed documentation
- See `README.md` for architecture overview
