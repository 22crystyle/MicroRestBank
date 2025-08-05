package com.example.shared.repository;

import com.example.shared.entity.Card;
import com.example.shared.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> getCardsByOwnerId(Long userId);

    Optional<Card> findByPan(String from);

    List<Card> findByExpiryDate(YearMonth now);

    Page<Card> findAllByOwner(User owner, Pageable pageable);
}
