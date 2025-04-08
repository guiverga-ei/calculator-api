# 🧮 Calculator API

This project is a distributed calculator application built with **Spring Boot** and **Apache Kafka**, designed to expose a RESTful API for basic arithmetic operations using **arbitrary precision decimal numbers**.

## 📚 Project Description

The application consists of two Spring Boot modules:

- **rest**: Exposes a REST API to receive requests and send them to Kafka.
- **calculator**: Listens to Kafka messages, processes the calculation, and returns the result.

Communication between the modules is handled asynchronously through **Apache Kafka**.

---

## ⚙️ Technologies Used

- Java 17
- Spring Boot 3.x
- Apache Kafka
- Docker & Docker Compose
- Maven
- SLF4J + Logback (with file appender)
- JUnit 5 (unit testing)

---

## 📦 Project Structure

```plaintext
calculator-api/
├── calculator/               # Microservice that processes calculations
│   ├── src/
│   ├── Dockerfile
│   └── application.properties
├── rest/                     # Microservice that exposes REST API
│   ├── src/
│   ├── Dockerfile
│   └── application.properties
├── docker-compose.yml        # Docker Compose config
└── pom.xml                   # Maven parent pom
```

---

## 🚀 How to Build and Run the Project

### 📌 Prerequisites

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/)
- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)

### 🧱 1. Build JAR files

Run the following command from the project root to generate JARs:

```bash
mvn clean install -DskipTests
```

### 🐳 2. Build and start all services with Docker Compose

```bash
docker-compose build
docker-compose up -d
```

This will:

- Start Zookeeper and Kafka containers
- Build and start the `rest` and `calculator` services
- Expose the REST API at: `http://localhost:8080`

---

## 📬 API Endpoints

All endpoints accept two decimal operands: `a` and `b`.

| Operation      | HTTP Endpoint                          |
|----------------|----------------------------------------|
| Sum            | `GET /api/sum?a=1&b=2`                 |
| Subtraction    | `GET /api/subtraction?a=5&b=3`         |
| Multiplication | `GET /api/multiplication?a=2&b=4`      |
| Division       | `GET /api/division?a=10&b=2`           |

### ✅ Example

#### Request:
```http
GET /api/sum?a=1.5&b=2.3
Accept: application/json
```

#### Response:
```json
{
  "result": 3.8
}
```

#### Division by zero:
```http
GET /api/division?a=5&b=0
```

```json
{
  "error": "Division by zero is not allowed"
}
```

---

## 📝 Logging

- SLF4J + Logback is used in both modules
- All log entries are saved to log files:
    - `serverlogs/rest.log`
    - `serverlogs/calculator.log`
- MDC propagation ensures that each log line includes the request ID for traceability

---

## 🧪 Running Tests

To run unit tests:

```bash
mvn test
```

---

## 🐾 Kafka

- Topics used:
    - `calculator-requests`
    - `calculator-responses`

---

