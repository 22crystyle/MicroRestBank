# RestBank: Microservices-Based Banking Application

RestBank is a comprehensive banking application built on a microservices architecture using Java and Spring Boot. It
provides a robust platform for managing users and bank cards through a RESTful API, featuring functionalities like card
creation, blocking, and funds transfer. The entire system is designed for containerized deployment using Docker.

## Table of Contents

- [Architecture](#architecture)
- [Technologies](#core-technologies)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Build and Run](#build-and-run)
- [User Management](#user-management)
- [API Documentation](#api-documentation)
- [Main Endpoints](#main-endpoints)

## Architecture

The application is composed of several specialized microservices that work together:

- **`api-gateway`**: Acts as the single entry point for all client requests. It handles request routing to the
  appropriate backend service, security, and other cross-cutting concerns using Spring Cloud Gateway.
- **`auth-service`**: Manages user authentication and authorization. It integrates with Keycloak for identity and access
  management.
- **`card-service`**: Contains all business logic related to bank cards, including creation, status management (
  activation, blocking), and financial transactions like transfers.
- **`customer-service`**: Responsible for managing customer data and synchronizing it with other services.
- **`eureka-server`**: A service discovery server that allows the microservices to locate and communicate with each
  other dynamically within the network.

Services communicate synchronously via REST APIs and asynchronously through Apache Kafka for event-driven updates,
utilizing Debezium for Change Data Capture (CDC) from the database.

### Core Technologies

- **Backend**: Java 21, Spring Boot 3
- **Database**: PostgreSQL
- **Messaging**: Apache Kafka
- **Change Data Capture**: Debezium
- **Security**: Keycloak
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Gradle
- **Containerization**: Docker, Docker Compose
- **Database Migration**: Liquibase

## Project Structure

The project is organized into several directories:

- `build-src`: Contains custom Gradle convention plugins.
- `docker`: Holds all Docker-related files, including `docker-compose.yml` configurations, Dockerfiles for each service,
  and initialization scripts.
- `gradle`: Contains the Gradle wrapper and version catalogs.
- `services`: Includes the source code for each microservice (`api-gateway`, `auth-service`, `card-service`,
  `customer-service`, `eureka-server`).
- `shared`: A module containing shared code, DTOs, and configurations used across multiple services.

## Prerequisites

To build and run this project, you will need:

- Java 21
- Docker
- Docker Compose

## Configuration

The primary configuration for the Docker environment is managed through the `.env` file located in the `docker/`
directory. This file sets environment variables for database credentials, Keycloak settings, and other service-specific
parameters.

## Build and Run

There are two primary methods for building and running the application.

### Automated Full Cycle (Recommended)

The project includes a custom Gradle task `fullCycleTime` that automates the entire process of building, deploying, and
verifying the application stack. This is the recommended approach.

**Prerequisite**: You must have `curl` installed and available in your system's PATH, as it is used for health checks.

To run the full cycle, execute this command from the project root:

```bash
./gradlew fullCycleTime
```

This single command will:

1. Build all service JAR files.
2. Tear down any existing Docker containers.
3. Start all services using `docker-compose`.
4. Wait for the `api-gateway` to become healthy.
5. Generate OpenAPI documentation.

### Manual Build and Run

If you prefer to run the steps manually, follow this sequence:

1. **Build the Docker Images**:
   From the project root, run the `bootJar` task to compile the code and create the JAR files for each service.
   ```bash
   ./gradlew bootJar
   ```

2. **Run with Docker Compose**:
   Navigate to the `docker` directory and use Docker Compose to start all the services.
   ```bash
   cd docker
   docker-compose up -d
   ```

## User Management

While regular users can register via the `POST /auth/register` endpoint, administrative users must be created through
the Keycloak Admin Console.

1. Navigate to the Keycloak Admin Console at [http://localhost:7080](http://localhost:7080).
2. Log in with the bootstrap admin credentials defined in your `docker/.env` file (`KC_ADMIN_USERNAME` and
   `KC_ADMIN_PASSWORD`).
3. Select the `bank-realm`.
4. Navigate to the `Users` section to create a new user.
5. After creating the user, go to the `Role Mappings` tab for that user and assign the `ADMIN` role.

## API Documentation

The API is documented using the OpenAPI 3.0 specification. Once the services are running, you can access the aggregated
Swagger UI through the API Gateway at:

[http://localhost:1024/docs](http://localhost:1024/docs)

The base URL for all API requests is `http://localhost:1024`.

## Main Endpoints

All endpoints are routed through the `api-gateway`.

### Authentication (`/auth`)

- `POST /login`: Authenticate a user and receive a JWT.
- `POST /register`: Register a new user.

### Customers (`/customers`)

- `GET /`: Get a paginated list of all customers (ADMIN).
- `GET /me`: Get details for the currently authenticated user (USER).
- `GET /{uuid}`: Get a specific customer by their UUID (ADMIN).

### Cards (`/cards`)

- `GET /`: Get a paginated list of cards. Admins see all (masked), users see their own (unmasked).
- `POST /`: Create a new card for a user (ADMIN).
- `GET /{id}`: Get a card by its ID.
- `POST /{id}/block-request`: Request to block a card (USER).
- `POST /{id}/block-approve`: Approve a card block request (ADMIN).
- `POST /{id}/block-reject`: Reject a card block request (ADMIN).
- `POST /transfer`: Transfer funds between two cards (USER).