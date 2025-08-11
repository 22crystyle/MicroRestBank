package com.example.customer.controller;

import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

   /* @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(CustomerRequest request) {

    }*/

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomerResponse> getCustomerByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(customerService.getCustomerByUUID(uuid));
    }
}
