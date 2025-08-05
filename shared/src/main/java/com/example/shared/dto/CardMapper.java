package com.example.shared.dto;

import com.example.shared.dto.response.CardResponse;
import com.example.shared.util.MaskingUtils;
import com.example.shared.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", imports = MaskingUtils.class)
public interface CardMapper {
    @Mapping(target = "pan", expression = "java(MaskingUtils.maskCardNumber(card.getPan()))")
    CardResponse toMaskedResponse(Card card);

    CardResponse toFullResponse(Card card);
}
