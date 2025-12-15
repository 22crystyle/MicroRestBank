package org.restbank.service.card.repository;

import org.restbank.service.card.entity.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository for managing {@link CardBlockRequest} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and includes custom query methods for finding block requests by card ID and status.</p>
 */
@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {

    /**
     * Finds a card block request by the card ID and the request status.
     *
     * @param cardId The ID of the card.
     * @param status The status of the block request.
     * @return An {@link Optional} containing the {@link CardBlockRequest} if found, or an empty optional.
     */
    Optional<CardBlockRequest> findByCard_IdAndStatus(Long cardId, CardBlockRequest.Status status);

    /**
     * Checks if a card block request exists for a given card ID and status.
     *
     * @param cardId The ID of the card.
     * @param status The status of the block request.
     * @return {@code true} if a matching request exists, {@code false} otherwise.
     */
    boolean existsCardBlockRequestByCard_IdAndStatus(Long cardId, CardBlockRequest.Status status);
}
