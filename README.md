# 🧮 Calculator API

This is a distributed calculator system built with **Spring Boot** and **Apache Kafka**. It provides a RESTful API to perform basic arithmetic operations using **arbitrary precision decimal numbers**.

---

## 📚 Overview

The project is composed of two independent Spring Boot services:

- **rest**: Exposes the REST API and sends calculation requests to Kafka.
- **calculator**: Listens to Kafka messages, performs the operations, and returns the result.

These services communicate asynchronously using **Apache Kafka**.

---

## ⚙️ Technologies

- Java 17
- Spring Boot 3.x
- Apache Kafka
- Docker & Docker Compose
- Maven
- SLF4J + Logback (with file-based logging)
- JUnit 5 (unit testing)

---

## 📁 Project Structure

```plaintext
calculator-api/
├── calculator/               # Microservice responsible for performing calculations
│   ├── src/
│   ├── Dockerfile
│   └── application.properties
├── rest/                     # Microservice that exposes the REST API
│   ├── src/
│   ├── Dockerfile
│   └── application.properties
├── docker-compose.yml        # Docker Compose configuration
└── pom.xml                   # Maven parent POM
```

---

## 🚀 How to Build and Run

### ✅ Prerequisites

Make sure the following are installed:

- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### 📦 1. Build the application

From the root directory of the project, run:

```bash
mvn clean install -DskipTests
```

This will generate the JAR files for both modules.

### 🐳 2. Start services using Docker Compose

```bash
docker-compose build
docker-compose up -d
```

This will:

- Start Zookeeper and Kafka containers
- Build and run the `rest` and `calculator` services
- Make the REST API available at: `http://localhost:8080`

---

## 📬 API Endpoints

All endpoints accept two decimal operands via query parameters: `a` and `b`.

| Operation      | HTTP Endpoint                          |
|----------------|----------------------------------------|
| Sum            | `GET /api/sum?a=1&b=2`                 |
| Subtraction    | `GET /api/subtraction?a=5&b=3`         |
| Multiplication | `GET /api/multiplication?a=2&b=4`      |
| Division       | `GET /api/division?a=10&b=2`           |

### 🔍 Example

**Request:**

```http
GET /api/sum?a=1.5&b=2.3
Accept: application/json
```

**Response:**

```json
{
  "result": 3.8
}
```

**Division by zero:**

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

- Both modules use SLF4J + Logback.
- Logs are written to rolling log files:
  - `ServerLogs/rest.log`
  - `ServerLogs/calculator.log`
- MDC (`Mapped Diagnostic Context`) is used to tag logs with a unique `requestId` to enable traceability across services.

---

## 🧪 Running Tests

To execute the unit tests for both modules:

```bash
mvn test
```

---

## 🐾 Kafka Topics

The following Kafka topics are used for communication:

- `calculator-requests`: receives operation requests
- `calculator-responses`: carries back the operation results

---

## 📌 Final Notes

- Logs and errors are properly traced using structured logging and unique identifiers.
- A simple message format is used for Kafka communication:  
  `"requestId,a,b,operation"`
- Everything runs via Docker with logs persisted through bind mounts (`./ServerLogs` folder).

---

## 👤 Author

**Guilherme Verga**  