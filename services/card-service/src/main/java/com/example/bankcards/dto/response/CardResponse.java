package com.example.bankcards.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class CardResponse extends RepresentationModel<CardResponse> {
    private final Long id;
    private final String pan;
    private final UserResponse user;
    private final CardStatusResponse status;
    private final BigDecimal balance;
}
