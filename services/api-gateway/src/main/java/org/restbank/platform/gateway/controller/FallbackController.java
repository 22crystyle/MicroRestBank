package org.restbank.platform.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to handle fallback responses for the circuit breaker.
 *
 * <p>When a downstream service is unavailable and the circuit breaker is open,
 * this controller provides a user-friendly fallback response.
 */
@RestController
public class FallbackController {

    /**
     * Provides a fallback response when a service is unavailable.
     *
     * @return A {@link ResponseEntity} with a service unavailable status and a message.
     */
    @GetMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service temporarily unavailable. Please try again later.");
    }
}
