package com.example.bankcards.dto.request;

import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.time.YearMonth;

public record CardRequest(
        Integer id,

        @Schema(
                description = "Номер карты, 16 цифр подряд без пробелов",
                example     = "1234567890123456",
                minimum     = "0000000000000000",      // доп. ограничения в swagger
                maximum     = "9999999999999999"
        )
        @Pattern(regexp = "\\d{16}", message = "Номер карты должен состоять из 16 цифр") String cardNumber,
        AccountResponse owner, // TODO: AccountRef - отдельный record для сохранения связи ссылок
        YearMonth expiryDate,
        CardStatus status
) {
}
