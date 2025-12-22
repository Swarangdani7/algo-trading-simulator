# Algorithmic Trading Simulator

An Intraday **algorithmic trading simulator** built using **Java, Spring Boot, Microservices, gRPC, and WebFlux**.

This project simulates an **algorithmic trading workflow** — including market price generation, strategy evaluation, trade execution, and portfolio tracking — **without using real money**.

---

## Key Features

- Simulated real-time stock prices
- Pluggable trading strategy engine (mean reversion using SMA)
- Automated BUY / SELL trade execution
- Portfolio management with holdings and P&L
- Simulation lifecycle management
- REST APIs for portfolio dashboard
- gRPC for low-latency inter-service communication
- Service discovery using Netflix Eureka

---

## Services Overview

### Market Service
- Simulates stock prices (e.g. TCS, INFY, etc.)
- Generates price updates at fixed intervals
- Streams prices to the Simulation Service using gRPC

### Strategy Service
- Receives price updates
- Applies a **time-based mean reversion strategy using SMA**
- Generates BUY / SELL / HOLD signals

### Simulation Service (Orchestrator)
- Starts and controls a simulation run
- Subscribes to market price streams
- Calls Strategy Service for trade decisions
- Sends trade intents to Portfolio Service
- Manages simulation lifecycle

### Portfolio Service
- Owns capital, holdings, and P&L
- Executes BUY / SELL trades
- Uses an **in-memory repository**

### Discovery Server (Eureka)
- Registers all services
- Enables dynamic service lookup
- Used by both REST and gRPC clients

---

## Flow

- Each simulation starts with **virtual capital (e.g. ₹1,00,000)**
- User allocates capital per stock
- Each allocation follows **one BUY → SELL cycle**
- Strategy emits trade intent
- Portfolio validates and executes trades
- Simulation stops after the trade cycle completes

---

## REST APIs

| Endpoint | Description |
|-------|------------|
`GET /market/prices` | Get all ticker prices |
`GET /market/prices/{ticker}` | Get specific ticker price |
`POST /simulation/start` | Start simulation |
`POST /simulation/stop/{simId}` | Stop simulation |
`GET /portfolio/summary` | Overall portfolio status |

---

## Tech Stack

| Category | Technology |
|-------|------------|
Language | Java 17 |
Framework | Spring Boot |
Reactive APIs | Spring WebFlux |
Inter-service Communication | gRPC |
Service Discovery | Netflix Eureka |
Build Tool | Maven |

