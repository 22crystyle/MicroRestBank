package com.example.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Customer Service application.
 *
 * <p>This service is responsible for managing all customer-related data, including personal
 * information and account status. It provides RESTful endpoints for creating, retrieving,
 * updating, and deleting customer records. The service integrates with Keycloak for
 * authentication and authorization and communicates with other microservices via Kafka
 * for event-driven data synchronization.</p>
 *
 * <p>The application is configured as a Spring Boot application and includes the necessary
 * components for web functionality, data persistence, and security. It scans for components
 * in both the local {@code com.example.customer} package and the shared
 * {@code com.example.shared} package to ensure common configurations and utilities are
 * available.</p>
 */
@SpringBootApplication(scanBasePackages = {"com.example.customer", "com.example.shared"})
public class CustomerApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}