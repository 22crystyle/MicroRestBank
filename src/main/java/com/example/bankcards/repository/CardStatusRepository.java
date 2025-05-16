package com.example.bankcards.repository;

import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardStatusRepository extends JpaRepository<CardStatus, Integer> {
}
