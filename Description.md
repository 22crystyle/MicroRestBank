# RestBank Project Description

## Project Overview

RestBank is a microservices-based banking application built with Java and Spring Boot. It provides a RESTful API for
managing users and bank cards, including features like card creation, blocking, and funds transfer. The entire system is
designed for containerized deployment using Docker.

## Microservices

The application consists of the following microservices:

* **`api-gateway`**: The single entry point for all client requests, handling routing, security, and other cross-cutting
  concerns. It is a reactive application built using Spring WebFlux and aggregates Swagger UI for all microservices.
* **`auth-service`**: Manages user authentication and authorization, integrated with Keycloak. It is a reactive
  application built using Spring WebFlux.
* **`card-service`**: Contains business logic for bank cards, including creation, status management, and transactions.
* **`customer-service`**: Manages customer data and synchronizes it with other services.
* **`eureka-server`**: A service discovery server for microservices to locate and communicate with each other.

## Technologies

* **Backend**: Java 21, Spring Boot 3, Spring WebFlux
* **Database**: PostgreSQL
* **Messaging**: Apache Kafka
* **Change Data Capture**: Debezium
* **Security**: Keycloak
* **Service Discovery**: Spring Cloud Netflix Eureka
* **API Gateway**: Spring Cloud Gateway
* **API Documentation**: OpenAPI/Swagger
* **Build Tool**: Gradle
* **Containerization**: Docker, Docker Compose
* **Database Migration**: Liquibase

## Principles and Patterns

* **Microservices Architecture**: The application is decomposed into small, independent services, each responsible for a
  specific business domain.
* **API Gateway**: A single entry point for all clients, providing a unified interface and handling cross-cutting
  concerns like security and routing.
* **Service Discovery**: Services register with a central registry (Eureka) and discover each other through it.
* **Centralized Configuration**: Configuration is externalized and managed in a central place.
* **Asynchronous Communication**: Services communicate asynchronously using Apache Kafka, which improves loose coupling
  and resilience.
* **Transactional Outbox Pattern**: The `customer-service` uses the outbox pattern to ensure reliable event publishing.
  Changes are written to an `outbox` table in the same transaction as the business data, and a separate process (
  Debezium) publishes these events to Kafka.
* **Change Data Capture (CDC)**: Debezium is used to capture row-level changes in the database and publish them as
  events to Kafka.
* **Circuit Breaker**: The `api-gateway` uses Resilience4j to implement the Circuit Breaker pattern, which prevents
  cascading failures when a downstream service is unavailable.
* **Database per Service**: Each microservice has its own private database schema, ensuring loose coupling between
  services.
* **Infrastructure as Code**: The entire environment is defined as code using Docker and Docker Compose, which allows
  for easy and repeatable deployments.

## Service Interactions

1. **Client Request**: A client sends a request to the `api-gateway`.
2. **Authentication**: The `api-gateway` can forward the request to the `auth-service` for authentication, or validate a
   JWT token. The `auth-service` interacts with Keycloak to manage users and authentication.
3. **Routing**: The `api-gateway` routes the request to the appropriate downstream service (`customer-service` or
   `card-service`) based on the request path.
4. **Business Logic**: The downstream service executes the business logic, potentially interacting with its database.
5. **Asynchronous Events**:
    * When a customer is created or updated in the `customer-service`, an event is written to the `outbox` table.
    * Debezium captures this change and publishes a message to a Kafka topic.
    * The `card-service` consumes this message and updates its own local copy of the user data.
6. **Service Discovery**: All services register with the `eureka-server` and use it to discover the locations of other
   services.

## Database Schema

* **`customer-service`**:
    * `customer_schema`:
        * `customers`: Stores customer information (name, email, phone, etc.).
        * `outbox`: Implements the Transactional Outbox pattern for reliable event publishing.
* **`card-service`**:
    * `card_schema`:
        * `users`: A local copy of user data, synchronized from the `customer-service` via Kafka.
        * `cards`: Stores bank card information (PAN, expiry date, status, balance) and is linked to a user.
        * `card_statuses`: A lookup table for card statuses.
        * `card_block_requests`: Stores requests to block a card.

## Conventional Plugins

The project uses custom Gradle plugins defined in the `build-src` directory to enforce conventions and simplify the
build process.

* **`java-convention`**: This plugin is applied to all Java subprojects and provides a common configuration for:
    * Java version (21)
    * Common dependencies (Spring Boot, Spring Cloud, Lombok, etc.)
    * Compiler options
    * Testing framework (JUnit 5)
    * OpenAPI documentation generation using Springdoc OpenAPI Gradle Plugin.
* **`full-cycle-time`**: This plugin provides a `fullCycleTime` task that automates the entire application lifecycle:
    1. Builds the application (`bootJar`).
    2. Stops and removes existing Docker containers (`docker-compose down`).
    3. Starts all services using Docker Compose (`docker-compose up`).
    4. Waits for the main services to become healthy.
    5. Generates OpenAPI documentation.
