

# 📐 Calculator API

A simple RESTful Calculator API using **Spring Boot** with modular architecture and **Apache Kafka** for inter-module communication.  
Supports basic arithmetic operations (`sum`, `subtraction`, `multiplication`, `division`) with **arbitrary precision decimal numbers**.

---

## 📦 Modules

- `rest`: Exposes the HTTP endpoints.
- `calculator`: Consumes requests via Kafka and performs the actual calculations.

---

## 🛠️ Technologies

- Java 20
- Spring Boot 3.4.4
- Apache Kafka
- SLF4J with Logback
- Docker & Docker Compose
- Maven (multi-module)
- JUnit 5

---

## 🚀 How to Build and Run

### 📦 Prerequisites

- Java 20+
- Maven
- Docker & Docker Compose

---

### 🔧 Build the Project

```bash
  mvn clean install
```

---

### 🐳 Docker: Build and Run

#### 1. **Build Docker images** for both modules:

```bash
  docker build -t calculator-module ./calculator
  docker build -t rest-module ./rest
```

#### 2. **Start Kafka, Zookeeper and both services**

```bash
  docker-compose up -d
```

---

## 📤 API Endpoints

Base URL: `http://localhost:8080/api`

### Examples:

#### ➕ Sum

```http
GET /api/sum?a=5&b=7
Accept: application/json
```

**Response:**
```json
{
  "result": 12
}
```

#### ❌ Division by zero

```http
GET /api/division?a=10&b=0
```

**Response:**
```json
{
  "error": "Division by zero is not allowed"
}
```

---

## 🧪 Unit Tests

Executa os testes:

```bash
  mvn test
```

Inclui testes para:
- `CalculatorService` (lógica de operações)
- `CalculatorController` (REST endpoints com mocks)

---

## 📄 Logging

- Configurado com **SLF4J + Logback**
- Cada módulo tem o seu ficheiro `logback.xml`
- Logs vão para a consola e ficheiro (pasta `log/`, configurável)

---

## 🐘 Docker Compose Overview

O `docker-compose.yml` inclui:

- Zookeeper
- Kafka
- Serviços da aplicação (se incluíres os módulos `rest` e `calculator` no ficheiro)

---

## 📂 Project Structure

```
calculator-api/
├── calculator/       # Kafka consumer + lógica de cálculo
│   └── Dockerfile
├── rest/             # REST API
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## 👤 Author

Guilherme Verga


