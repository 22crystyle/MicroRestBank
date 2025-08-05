package com.example.shared.repository;

import com.example.shared.entity.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
    Optional<CardBlockRequest> findByCardIdAndStatus(Long cardId, CardBlockRequest.Status status);

    boolean existsCardBlockRequestByCardIdAndStatus(Long cardId, CardBlockRequest.Status status);
}
