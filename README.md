# Intelligent Data Aggregation Platform (IDAP)

A production-grade microservices platform that fetches real-time data from multiple external APIs, processes through event-driven microservices, and displays unified insights through micro-frontends.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              MICRO-FRONTENDS                                 │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │  Shell   │ │ Weather  │ │Financial │ │   News   │ │  Admin   │          │
│  │   App    │ │Dashboard │ │Dashboard │ │   Feed   │ │  Panel   │          │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘          │
└───────┼────────────┼────────────┼────────────┼────────────┼─────────────────┘
        │            │            │            │            │
        └────────────┴────────────┴────────────┴────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │       API GATEWAY         │
                    │    (JWT Auth + Routing)   │
                    │        Port: 8080         │
                    └─────────────┬─────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼───────┐  ┌──────────────▼──────────────┐  ┌───────▼───────┐
│ Config Server │  │      Eureka Discovery       │  │    Services   │
│  Port: 8888   │  │        Port: 8761           │  │  8081-8086    │
└───────────────┘  └─────────────────────────────┘  └───────────────┘
```

## 🛠️ Technology Stack

| Category | Technology |
|----------|------------|
| **Backend** | Spring Boot 3.2, Java 21 |
| **Frontend** | React 18, Webpack Module Federation |
| **Cloud** | Spring Cloud Config, Eureka, Gateway |
| **Databases** | PostgreSQL, MySQL, MongoDB, Redis |
| **Messaging** | Apache Kafka, RabbitMQ |
| **Security** | Spring Security, JWT |
| **Observability** | Zipkin, ELK Stack, Prometheus, Grafana |

## 📦 Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| Config Server | 8888 | - | Centralized configuration |
| Eureka Server | 8761 | - | Service discovery |
| API Gateway | 8080 | - | Routing, auth, rate limiting |
| User Service | 8086 | PostgreSQL | Authentication, user management |
| Weather Service | 8081 | PostgreSQL | OpenWeatherMap integration |
| Financial Service | 8082 | MySQL | Stock/crypto data (Finnhub) |
| News Service | 8083 | MongoDB | News aggregation (NewsAPI) |
| Analytics Service | 8084 | MongoDB | Data correlation, CQRS |
| Notification Service | 8085 | - | Email/push notifications |

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven Daemon (mvnd)
- Docker Desktop
- Node.js 22+

### 1. Start Infrastructure

```bash
cd docker
docker-compose up -d
```

### 2. Build All Services

```bash
mvnd clean install -DskipTests
```

### 3. Start Services (in order)

```bash
# Terminal 1: Config Server
cd config-server && mvnd spring-boot:run

# Terminal 2: Eureka Server (wait for config server)
cd eureka-server && mvnd spring-boot:run

# Terminal 3: API Gateway
cd api-gateway && mvnd spring-boot:run

# Terminal 4: User Service
cd user-service && mvnd spring-boot:run

# Terminal 5: Weather Service
cd weather-service && mvnd spring-boot:run
```

### 4. Access Services

- **Eureka Dashboard**: <http://localhost:8761>
- **API Gateway**: <http://localhost:8080>
- **RabbitMQ Management**: <http://localhost:15672> (idap_admin/idap_secret_2024)

## 📡 API Endpoints

### Authentication

```bash
# Register
POST /api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}

# Login
POST /api/auth/login
{
  "username": "john",
  "password": "password123"
}
```

### Weather

```bash
# Get current weather (requires JWT)
GET /api/weather/current?city=London
Authorization: Bearer <token>

# Get forecast
GET /api/weather/forecast?city=London&days=5
Authorization: Bearer <token>
```

## 🔑 External APIs

| API | Free Tier | Environment Variable |
|-----|-----------|---------------------|
| OpenWeatherMap | 1000 calls/day | `OPENWEATHER_API_KEY` |
| Finnhub | 60 calls/min | `FINNHUB_API_KEY` |
| NewsAPI | 100 requests/day | `NEWSAPI_API_KEY` |
| CoinGecko | Unlimited | - |
| SendGrid | 100 emails/day | `SENDGRID_API_KEY` |

## 📁 Project Structure

```
├── docker/
│   ├── docker-compose.yml
│   └── init-scripts/
├── config-server/
├── eureka-server/
├── api-gateway/
├── user-service/
├── weather-service/
├── financial-service/      (Phase 2)
├── news-service/           (Phase 2)
├── analytics-service/      (Phase 2)
├── notification-service/   (Phase 2)
├── frontend/               (Phase 4)
│   ├── shell-app/
│   ├── weather-dashboard/
│   ├── financial-dashboard/
│   ├── news-feed/
│   └── admin-panel/
└── pom.xml
```

## 📝 License

MIT License - feel free to use for learning and portfolio purposes.
