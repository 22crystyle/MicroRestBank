package org.restbank.platform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the API Gateway application.
 *
 * <p>This service acts as the single entry point for all client requests to the RestBank
 * microservices ecosystem. It is built using Spring Cloud Gateway and is responsible for
 * routing incoming requests to the appropriate downstream service (e.g., `auth-service`,
 * `customer-service`, `card-service`).</p>
 *
 * <p>In addition to routing, the API Gateway handles cross-cutting concerns such as:
 * <ul>
 *     <li><b>Security:</b> Enforcing authentication and authorization by validating JWTs.</li>
 *     <li><b>Rate Limiting and Resilience:</b> Protecting services from being overwhelmed
 *         using patterns like circuit breakers.</li>
 *     <li><b>API Composition:</b> Aggregating results from multiple services.</li>
 *     <li><b>Centralized Documentation:</b> Providing a unified Swagger UI for all backend APIs.</li>
 * </ul>
 * This centralization simplifies client interactions and enhances the security and
 * manageability of the overall system.</p>
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}