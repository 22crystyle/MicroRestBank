package com.example.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Customer Service application.
 * This class configures and runs the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = {"com.example.customer", "com.example.shared"})
public class CustomerApplication {
    /**
     * Main method to start the Customer Service application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}