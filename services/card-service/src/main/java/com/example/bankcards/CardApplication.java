package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main entry point for the Card Service application.
 *
 * <p>This service manages all business logic related to bank cards. It provides a comprehensive
 * set of RESTful endpoints for card operations, including:
 * <ul>
 *     <li>Creating new cards for customers.</li>
 *     <li>Retrieving card details (with appropriate masking for security).</li>
 *     <li>Managing card status, such as blocking and unblocking.</li>
 *     <li>Processing financial transactions, like transferring funds between cards.</li>
 * </ul>
 * The service also includes scheduled tasks for handling card expiry and consumes events from
 * Kafka to stay synchronized with customer data from other services. It uses Spring Data JPA
 * for persistence and is secured with Spring Security.</p>
 *
 * <p>The {@code @EnableScheduling} annotation is used to run periodic tasks, such as checking for
 * expired cards, while {@code @EnableRetry} provides resilience for database operations.</p>
 */
@EnableScheduling
@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.example.bankcards", "com.example.shared"})
public class CardApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
    }
}
