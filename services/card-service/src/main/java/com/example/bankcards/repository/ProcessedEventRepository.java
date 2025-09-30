package com.example.bankcards.repository;

import com.example.bankcards.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * A repository for managing {@link ProcessedEvent} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations for
 * tracking events that have been processed by the service, ensuring idempotency in event handling.</p>
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
}
