package com.example.customer.service;

import com.example.customer.dto.CustomerMapper;
import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.entity.Customer;
import com.example.customer.exception.CustomerNotFound;
import com.example.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByUUID(UUID uuid) {
        Customer customer = customerRepository.findById(uuid)
                .orElseThrow(() -> new CustomerNotFound(uuid));
        return customerMapper.toResponse(customer);
    }
}
