package org.restbank.service.customer.repository;

import org.restbank.service.customer.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for OutboxEvent entities.
 * Provides CRUD operations for OutboxEvent data.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}
