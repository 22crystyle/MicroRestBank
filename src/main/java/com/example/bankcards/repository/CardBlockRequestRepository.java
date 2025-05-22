package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
}
