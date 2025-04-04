# Calculator API

## Project Description

This project is a RESTful API designed to provide basic functionalities of a calculator, including operations for addition, subtraction, multiplication, and division. It supports only two operands per operation and is capable of handling arbitrary precision decimal numbers.

## Technologies Used

- **Spring Boot**: Used to simplify the configuration and development of the project.
- **Apache Kafka**: Employed for asynchronous communication between different modules of the application.
- **Docker**: Used for containerization of the application and dependent services like Kafka and Zookeeper.
- **Maven**: Dependency management and build automation.

## Architecture

The application is divided into two main modules:
- **calculator**: Module responsible for the calculation logic.
- **rest**: Module that exposes the RESTful API and handles HTTP requests.

Communication between the modules is done via Apache Kafka, ensuring that the application can be scaled easily and maintains a clear separation between presentation logic and business logic.

## Local Setup and Execution with Docker

### Prerequisites
- Docker
- Docker Compose

### Execution Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/guiverga-ei/calculator-api.git
