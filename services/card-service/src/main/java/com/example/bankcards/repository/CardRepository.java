package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByExpiryDate(YearMonth now);

    Page<Card> findAllByUser(User user, Pageable pageable);

    boolean existsByIdAndUser_Id(Long cardId, UUID userId);
}
