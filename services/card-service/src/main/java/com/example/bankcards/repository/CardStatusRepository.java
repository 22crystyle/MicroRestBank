package com.example.bankcards.repository;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CardStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository for managing {@link CardStatus} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations and includes
 * a custom query method for finding a card status by its name.</p>
 */
@Repository
public interface CardStatusRepository extends JpaRepository<CardStatus, Integer> {

    /**
     * Finds a card status by its name.
     *
     * @param name The name of the status to find, as defined in the {@link CardStatusType} enum.
     * @return An {@link Optional} containing the {@link CardStatus} if found, or an empty optional.
     */
    Optional<CardStatus> findByName(CardStatusType name);
}