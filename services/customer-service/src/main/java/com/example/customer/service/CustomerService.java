package com.example.customer.service;

import com.example.customer.dto.CustomerMapper;
import com.example.customer.dto.request.CustomerRequest;
import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.entity.Customer;
import com.example.customer.entity.CustomerStatus;
import com.example.customer.exception.CustomerNotFound;
import com.example.customer.repository.CustomerRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreated_at(Instant.now());
        customer.setUpdated_at(Instant.now());
        customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByUUID(UUID uuid) {
        Customer customer = customerRepository.findById(uuid)
                .orElseThrow(() -> new CustomerNotFound(uuid));
        return customerMapper.toResponse(customer);
    }
}
