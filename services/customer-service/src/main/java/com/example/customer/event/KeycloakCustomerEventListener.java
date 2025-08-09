package com.example.customer.event;

import com.example.customer.dto.request.CustomerRequest;
import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakCustomerEventListener {
    private final ObjectMapper objectMapper;
    private final CustomerService customerService;

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
                CustomerResponse response = customerService.createCustomer(json);
                log.info("Keycloak Admin Listener: Create User {}", response);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
