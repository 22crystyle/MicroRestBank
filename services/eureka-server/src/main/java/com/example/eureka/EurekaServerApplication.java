package com.example.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * The main entry point for the Eureka Service Discovery Server.
 *
 * <p>This application acts as the central service registry for the RestBank microservices
 * ecosystem. All other services register themselves with this server, allowing them to
 * discover and communicate with each other dynamically. The {@code @EnableEurekaServer}
 * annotation activates the Eureka server functionality, turning this Spring Boot
 * application into a service discovery hub.</p>
 *
 * <p>By maintaining a registry of all running service instances, Eureka plays a critical
 * role in the resilience and scalability of the microservices architecture, enabling
 * load balancing and failover capabilities.</p>
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}
