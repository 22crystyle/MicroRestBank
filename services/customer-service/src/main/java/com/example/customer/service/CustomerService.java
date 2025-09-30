package com.example.customer.service;

import com.example.customer.dto.CustomerMapper;
import com.example.customer.dto.request.CustomerRequest;
import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.entity.Customer;
import com.example.customer.entity.OutboxEvent;
import com.example.customer.exception.CustomerNotFound;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.repository.OutboxEventRepository;
import com.example.shared.dto.event.CustomerCreatedEvent;
import com.example.shared.dto.event.CustomerStatus;
import com.example.shared.dto.event.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service class for managing customer-related business logic.
 * Handles operations such as saving, retrieving, and listing customers.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    /**
     * Saves a new customer or updates an existing one.
     * Publishes a CustomerCreatedEvent to the outbox for asynchronous processing.
     * @param request The CustomerRequest containing customer data.
     * @return The saved or updated CustomerResponse.
     * @throws JsonProcessingException if there's an error processing JSON for the event.
     */
    @Transactional
    public CustomerResponse saveCustomer(CustomerRequest request) throws JsonProcessingException {
        Customer customer = customerMapper.toEntity(request);
        if (request.getId() != null) {
            customer.setId(request.getId());
        } else {
            customer.setId(UUID.randomUUID());
        }
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        customerRepository.save(customer);

        CustomerCreatedEvent event = new CustomerCreatedEvent(
                customer.getId(),
                customer.getStatus()
        );

        OutboxEvent outbox = OutboxEvent.builder()
                .aggregateType("Customer")
                .aggregateId(customer.getId().toString())
                .eventType(EventType.CUSTOMER_CREATED)
                .payload(objectMapper.writeValueAsString(event))
                .createdAt(Instant.now())
                .build();
        outboxEventRepository.save(outbox);
        outboxEventRepository.flush();
        return customerMapper.toResponse(customer);
    }

    /**
     * Retrieves a customer by their unique identifier.
     * @param uuid The UUID of the customer to retrieve.
     * @return The CustomerResponse corresponding to the given UUID.
     * @throws CustomerNotFound if no customer is found with the given UUID.
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByUUID(UUID uuid) {
        Customer customer = customerRepository.findById(uuid)
                .orElseThrow(() -> new CustomerNotFound(uuid));
        return customerMapper.toResponse(customer);
    }

    /**
     * Retrieves a paginated list of all customers.
     * @param pageable The pagination information.
     * @return A Page of CustomerResponse objects.
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }
}
