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


## Logging Configuration

This project utilizes Logback for logging, which is a versatile and flexible framework that allows detailed logging across different levels. Each module (`calculator` and `rest`) contains a `logback.xml` configuration file, which is set up to handle the logging requirements efficiently.

### Levels of Logging

The logging system is set up to capture a range of information from the following levels:

- **TRACE**: For extremely detailed outputs, used predominantly during development to trace the application's execution flow.
- **DEBUG**: Provides detailed information useful for diagnosing problems during development.
- **INFO**: Logs normal operation messages that are helpful in a production environment to track the application's behavior.
- **WARN**: Indicates possible issues that do not currently affect the application but might lead to future errors.
- **ERROR**: Captures serious issues where the application's functionality is impaired.

### Output

Logs are directed to both the console and log files, ensuring that information is available for real-time monitoring and post-analysis. Log files are rotated daily to manage disk space effectively.

### Importance of Logging

Adequate logging is crucial for diagnosing issues quickly and efficiently, allowing for faster fixes and ensuring system stability. The configured logging levels help in distinguishing between critical errors and informational messages, making the logs a valuable tool for ongoing maintenance and development.

