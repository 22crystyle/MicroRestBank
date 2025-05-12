package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<BankCard, Integer> {

}
