package org.restbank.service.card.repository;

import org.restbank.service.card.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * A repository for managing {@link User} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations for
 * user entities within the card service. The user data is synchronized from the customer
 * service via events.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
