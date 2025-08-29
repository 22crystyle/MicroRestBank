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
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper = new ObjectMapper(JsonFactory.builder().build());
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) throws JsonProcessingException {
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

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByUUID(UUID uuid) {
        Customer customer = customerRepository.findById(uuid)
                .orElseThrow(() -> new CustomerNotFound(uuid));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }
}