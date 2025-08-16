package com.example.bankcards.event;

import com.example.bankcards.entity.ProcessedEvent;
import com.example.bankcards.repository.ProcessedEventRepository;
import com.example.bankcards.service.UserService;
import com.example.shared.dto.event.CustomerCreatedEvent;
import com.example.shared.util.EventType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper(JsonFactory.builder().build());
    private final UserService userService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "restbank.customer_schema.outbox", groupId = "user-sync-group")
    public void listen(String message) {
        log.info(message);
        try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode after = root.path("payload").path("after");
            if (after.isMissingNode() || after.isNull()) {
                after = root;
            }

            String eventTypeStr = after.path("event_type").asText(null);
            EventType eventType = null;
            if (eventTypeStr != null) {
                try {
                    eventType = EventType.valueOf(eventTypeStr);
                } catch (IllegalArgumentException ex) {
                    log.info("Unknown EventType name: {}", eventTypeStr);
                }
            }

            if (!EventType.CUSTOMER_CREATED.equals(eventType)) return;

            String outboxId = after.path("id").asText();
            if (outboxId == null || outboxId.isEmpty()) outboxId = after.path("aggregate_id").asText();

            if (processedEventRepository.existsById(UUID.fromString(outboxId))) {
                return;
            }

            JsonNode payloadNode = after.path("payload");
            if (payloadNode.isTextual()) {
                payloadNode = objectMapper.readTree(payloadNode.asText());
            }

            CustomerCreatedEvent event = objectMapper.treeToValue(payloadNode, CustomerCreatedEvent.class);
            userService.applyCustomerCreated(event);
            processedEventRepository.save(new ProcessedEvent(event.getId(), outboxId, eventType, Instant.now()));

        } catch (JsonProcessingException ex) {
            log.error("Failed to process outbox event", ex);
            throw new RuntimeException(ex);
        }
    }
}
