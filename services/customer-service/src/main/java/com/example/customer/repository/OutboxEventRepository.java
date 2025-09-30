package com.example.customer.repository;

import com.example.customer.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for OutboxEvent entities.
 * Provides CRUD operations for OutboxEvent data.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}
