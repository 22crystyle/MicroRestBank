# Bank_REST

Bank_REST is a backend application built with Java and Spring Boot for managing bank accounts and cards. It provides a
RESTful API for creating, retrieving, updating, and deleting accounts and cards, as well as handling card block requests
and money transfers between cards. The application uses Spring Security for authentication and authorization, and
PostgreSQL as the database, with Liquibase for database migrations.

## Features

- **Account Management**: Create, retrieve, and delete user accounts (Admin role required).
- **Card Management**: Create, retrieve, and manage bank cards, including blocking and unblocking cards.
- **Card Block Requests**: Users can request to block a card, and admins can approve or reject these requests.
- **Money Transfers**: Transfer money between cards owned by the same user.
- **Security**: Role-based access control with ADMIN and USER roles, using Basic Authentication.
- **Pagination**: Retrieve paginated lists of accounts and cards.
- **Localization**: Support for English and Russian locales.
- **Scheduled Tasks**: Automatically mark cards as expired based on their expiry date.
- **Swagger UI**: API documentation available at `/docs`.

## Prerequisites

To run this application, ensure you have the following installed:

- **Java**: 21 (AdoptOpenJDK or Eclipse Temurin recommended)
- **Docker**: For running the application with PostgreSQL
- **Maven**: For building the project
- **PostgreSQL**: Version 17.5 or compatible (if not using Docker)
- **Git**: For cloning the repository

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/22crystyle/Bank_REST.git
   cd Bank_REST
   ```

2. **Build the Project**:
   ```bash
   ./mvnw clean package
   ```

3. **Set Up Environment Variables**:
   Edit a `.env` file in the `docker/` directory (or use the provided example):
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/restbank
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=postgres
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   SPRING_JPA_SHOW_SQL=true
   SPRING_PROFILES_ACTIVE=prod
   SERVER_PORT=1024
   ```

4. **Run with Docker**:
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

   This will start the Spring Boot application and a PostgreSQL database. The application will be accessible at
   `http://localhost:1024`.

5. **Access Swagger UI**:
   Open `http://localhost:1024/docs` in your browser to view the API documentation.

## API Endpoints

The API is documented using OpenAPI 3.1.0 and can be explored via Swagger UI at `/docs`. Key endpoints include:

- **Accounts**:
    - `GET /api/v1/accounts`: Retrieve a paginated list of accounts (Admin only).
    - `GET /api/v1/accounts/{id}`: Get user details by ID (Admin only).
    - `POST /api/v1/accounts`: Create a new user (Admin only).
    - `DELETE /api/v1/accounts/{id}`: Delete an user by ID (Admin only).
    - `GET /api/v1/accounts/{id}/cards`: Get all cards for an user (Admin only).

- **Cards**:
    - `GET /api/v1/cards`: Retrieve a paginated list of cards (Admin only).
    - `GET /api/v1/cards/{id}`: Get card details by ID (Full details for owners, masked for others).
    - `POST /api/v1/cards`: Create a new card for a user (Admin only).
    - `POST /api/v1/cards/transfer`: Transfer money between cards (User only).
    - `POST /api/v1/cards/{id}/block-request`: Request to block a card (User only).
    - `POST /api/v1/cards/{id}/block-approve`: Approve a card block request (Admin only).
    - `POST /api/v1/cards/{id}/block-reject`: Reject a card block request (Admin only).

## Authentication

The application uses **Basic Authentication**. Roles are defined as:

- **ADMIN**: Can manage accounts and cards, approve/reject block requests.
- **USER**: Can view their own cards, request card blocks, and transfer money between their cards.

Default accounts (defined in `006-init-admin.yaml`):

- Admin: `username: admin`, `password: admin`
- User: `username: user`, `password: user`

## Database

The application uses PostgreSQL with Liquibase for schema management. The database schema is defined in
`src/main/resources/db/migration/`. Initial data includes roles (`ADMIN`, `USER`) and card statuses (`ACTIVE`,
`BLOCKED`, `EXPIRED`).

### Schema Overview

- **roles**: Stores user roles (e.g., ADMIN, USER).
- **card_statuses**: Stores card status types (ACTIVE, BLOCKED, EXPIRED).
- **accounts**: Stores user user details (username, password, role, etc.).
- **bank_cards**: Stores card details (card number, owner, balance, status, expiry date).
- **card_block_requests**: Stores card block requests (card, status, created/processed timestamps).

## Configuration

Key configuration files:

- `src/main/resources/application.yml`: Main Spring Boot configuration (database, port, Swagger, etc.).
- `docker/.env`: Environment variables for Docker.
- `docker/docker-compose.yml`: Defines services for the application and PostgreSQL.

## Running Tests

The project includes unit and integration tests using JUnit, Mockito, and Spring Boot Test. To run tests:

```bash
./mvnw test
```

Key test files:

- `AccountControllerTest.java`: Unit tests for user-related endpoints.
- `CardControllerTest.java`: Unit tests for card-related endpoints.
- `AccountControllerIntegrationTest.java`: Integration tests for user endpoints.
- `AccountServiceTest.java`: Unit tests for user service logic.

## Contact

- **Email**: shimorowm@gmail.com
- **GitHub**: [22crystyle](https://github.com/22crystyle)
