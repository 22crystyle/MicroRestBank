package org.restbank.service.card.repository;

import org.restbank.service.card.entity.Card;
import org.restbank.service.card.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * A repository for managing {@link Card} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations and includes
 * custom query methods for finding cards by expiry date, owner, and for checking ownership.</p>
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Finds all cards that have a specific expiry date.
     *
     * @param now The {@link YearMonth} representing the expiry date to search for.
     * @return A list of cards that expire in the given month and year.
     */
    List<Card> findByExpiryDate(YearMonth now);

    /**
     * Finds all cards belonging to a specific user, with pagination.
     *
     * @param user     The user whose cards are to be retrieved.
     * @param pageable The pagination information.
     * @return A {@link Page} of cards belonging to the specified user.
     */
    Page<Card> findAllByUser(User user, Pageable pageable);

    /**
     * Checks if a card with a specific ID is owned by a user with the given UUID.
     *
     * @param cardId The ID of the card.
     * @param userId The UUID of the user.
     * @return {@code true} if the card exists and is owned by the user, {@code false} otherwise.
     */
    boolean existsByIdAndUser_Id(Long cardId, UUID userId);
}
