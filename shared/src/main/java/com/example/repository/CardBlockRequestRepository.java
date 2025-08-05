package com.example.repository;

import com.example.entity.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
    Optional<CardBlockRequest> findByCardIdAndStatus(Long cardId, CardBlockRequest.Status status);

    boolean existsCardBlockRequestByCardIdAndStatus(Long cardId, CardBlockRequest.Status status);
}
