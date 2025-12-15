package org.restbank.service.customer.repository;

import org.restbank.service.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for Customer entities.
 * Provides CRUD operations and custom queries for Customer data.
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
