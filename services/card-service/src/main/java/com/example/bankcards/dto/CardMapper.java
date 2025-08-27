package com.example.bankcards.dto;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.MaskingUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", imports = MaskingUtils.class)
public interface CardMapper {
    @Mapping(target = "pan", expression = "java(MaskingUtils.maskCardNumber(card.getPan()))")
    CardResponse toMaskedResponse(Card card);

    CardResponse toFullResponse(Card card);
}
