package org.restbank.platform.auth;

import org.restbank.platform.auth.config.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * The main entry point for the Authentication Service.
 *
 * <p>This service is responsible for handling user authentication and registration. It acts as a
 * bridge between the RestBank application and the Keycloak identity and access management
 * server. It provides RESTful endpoints for user login and registration, interacting with
 * Keycloak's Admin API to create and manage users.</p>
 *
 * <p>Key responsibilities include:
 * <ul>
 *     <li>Exposing {@code /login} and {@code /register} endpoints.</li>
 *     <li>Validating user credentials against Keycloak.</li>
 *     <li>Orchestrating the user creation process in Keycloak upon registration.</li>
 *     <li>Issuing access tokens upon successful authentication.</li>
 * </ul>
 * The application is built on Spring WebFlux for a reactive, non-blocking architecture and
 * is secured using Spring Security.</p>
 */
@SpringBootApplication(scanBasePackages = {"org.restbank"})
@EnableConfigurationProperties(KeycloakProperties.class)
public class AuthApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}