package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsCardByCardNumber(String number);

    List<Card> getCardsByOwnerId(Long userId);

    Optional<Card> findByCardNumber(String from);

    boolean existsCardByCardNumberAndOwner_Username(String cardNumber, String name);

    boolean existsCardByIdAndOwner_Username(Long cardId, String name);
}
