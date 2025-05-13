package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardDto toDto(Card card);
    Card toEntity(CardDto cardDto);
}
