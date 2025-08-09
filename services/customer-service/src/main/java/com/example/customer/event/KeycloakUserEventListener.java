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

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserEventListener {
    private final ObjectMapper objectMapper;
    private final CustomerService customerService;

    @KafkaListener(topics = "keycloak-admin-events", groupId = "user-sync-group")
    public void listenUserCreation(String message) {
        try {
            AdminEvent adminEvent = objectMapper.readValue(message, AdminEvent.class);
            if (adminEvent.getResourceType().equals(ResourceType.USER)
                    && adminEvent.getOperationType().equals(OperationType.CREATE)) {
                CustomerRequest json = objectMapper.readValue(adminEvent.getRepresentation(), CustomerRequest.class);
                CustomerResponse response = customerService.createCustomer(json);
                log.info(String.valueOf(response));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
