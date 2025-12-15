package org.restbank.service.customer.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.restbank.service.customer.dto.request.CustomerRequest;
import org.restbank.service.customer.dto.response.CustomerResponse;
import org.restbank.service.customer.service.CustomerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Kafka listener for Keycloak admin events, specifically for customer creation and updates.
 * This class processes events from Keycloak to synchronize customer data within the service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakCustomerEventListener {
    private final ObjectMapper objectMapper;
    private final CustomerService customerService;

    /**
     * Listens for Keycloak admin events on the "keycloak-admin-events" topic.
     * Processes user creation and update events to save or update customer information.
     *
     * @param message The Kafka message containing the AdminEvent JSON.
     */
    @KafkaListener(topics = "keycloak-admin-events", groupId = "user-sync-group")
    public void listenCustomerCreation(String message) {
        try {
            AdminEvent adminEvent = objectMapper.readValue(message, AdminEvent.class);
            if (adminEvent.getResourceType().equals(ResourceType.USER) && (
                    adminEvent.getOperationType().equals(OperationType.CREATE) ||
                            adminEvent.getOperationType().equals(OperationType.UPDATE))
            ) {
                CustomerRequest json = objectMapper.readValue(adminEvent.getRepresentation(), CustomerRequest.class);
                String uuidString = adminEvent.getResourcePath().replace("users/", "");
                json.setId(UUID.fromString(uuidString));
                log.info("Keycloak Admin Listener: Received json {}", json);
                CustomerResponse response = customerService.saveCustomer(json);
                log.info("Keycloak Admin Listener: Create User {}", response);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO: SonarQube
        }
    }
}
