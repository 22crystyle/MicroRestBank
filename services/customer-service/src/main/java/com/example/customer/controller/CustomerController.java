package com.example.customer.controller;

import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> getCustomerByUUID(@PathVariable UUID uuid) {
        return ResponseEntity.ok(customerService.getCustomerByUUID(uuid));
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getCurrentCustomer() {
        return null; // TODO: getCurrentCustomer()
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> pagination(
            @RequestParam int size,
            @RequestParam int page
    ) {
        return null; // TODO: pagination
    }
}
