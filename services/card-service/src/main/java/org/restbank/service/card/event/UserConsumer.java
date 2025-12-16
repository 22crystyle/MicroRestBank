package org.restbank.service.card.event;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restbank.libs.api.dto.event.CustomerCreatedEvent;
import org.restbank.libs.api.dto.event.EventType;
import org.restbank.service.card.entity.ProcessedEvent;
import org.restbank.service.card.repository.ProcessedEventRepository;
import org.restbank.service.card.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * A Kafka consumer responsible for listening to user-related events.
 *
 * <p>This service listens to the "restbank.customer_schema.outbox" topic for events
 * originating from the customer service. It processes these events to keep the local
 * user data synchronized, particularly handling the creation of new customers.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper(JsonFactory.builder().build());
    private final UserService userService;
    private final ProcessedEventRepository processedEventRepository;

    /**
     * Listens for messages on the specified Kafka topic and processes them.
     *
     * <p>This method is triggered whenever a new message is available on the
     * "restbank.customer_schema.outbox" topic. It parses the message to extract the event
     * details, checks for duplicate events, and if the event is a {@link EventType#CUSTOMER_CREATED}
     * event, it delegates the processing to the {@link UserService}.</p>
     *
     * @param message The raw message content from Kafka as a JSON string.
     * @throws RuntimeException if the message cannot be parsed.
     */
    @KafkaListener(topics = "restbank.customer_schema.outbox", groupId = "user-sync-group")
    public void listen(String message) {
        log.info(message);
        try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode after = root.path("payload").path("after");
            if (after.isMissingNode() || after.isNull()) {
                after = root;
            }

            Optional<EventType> eventTypeOpt =
                    EventType.from(after.path("event_type").asText(null));

            EventType eventType = eventTypeOpt.orElse(null);

            if (!EventType.CUSTOMER_CREATED.equals(eventType)) {
                return;
            }

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
            log.error("Failed to process restbank.customer_schema.outbox", ex);
        }
    }
}
