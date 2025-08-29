# RestBank

RestBank is a microservices-based banking application built with Java and Spring Boot. It provides RESTful APIs for
managing users and bank cards, including features like card creation, blocking, and funds transfer. The project is
designed to be run in a containerized environment using Docker.

## Technologies

* **Backend**: Java 21, Spring Boot 3
* **Database**: PostgreSQL
* **Messaging**: Apache Kafka
* **Change Data Capture**: Debezium
* **Security**: Keycloak
* **Service Discovery**: Spring Cloud Netflix Eureka
* **API Gateway**: Spring Cloud Gateway
* **Build Tool**: Gradle
* **Containerization**: Docker, Docker Compose
* **Database Migration**: Liquibase

## Getting Started

### Prerequisites

* Java 21
* Docker
* Docker Compose

### Building the Project

To build the project and create the Docker images, run the following command from the root directory:

```bash
./gradlew bootJar
```

### Running the Application

The entire application can be started with Docker Compose:

```bash
docker-compose up -d
```

This will start all the services, including the database, Kafka, and Keycloak.

## API

All endpoints are accessible through the API Gateway. The base URL for all requests is `http://localhost:1024`.

For detailed information about the API, please refer to the `docs/openapi.yaml` file.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
