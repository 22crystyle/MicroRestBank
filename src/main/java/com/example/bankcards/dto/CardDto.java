package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    @NotNull private Long id;
    @NotBlank private String number;
    private YearMonth expiryDate;
    @NotNull private AccountDto owner;
    @NotNull private CardStatus status;
}
