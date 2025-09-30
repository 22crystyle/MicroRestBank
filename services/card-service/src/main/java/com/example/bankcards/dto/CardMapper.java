package com.example.bankcards.dto;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.MaskingUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * A MapStruct mapper for converting between {@link Card} entities and {@link CardResponse} DTOs.
 *
 * <p>This interface defines methods for transforming card data, including a method for creating a
 * response with a masked PAN (Primary Account Number) for security purposes.</p>
 */
@Mapper(componentModel = "spring", imports = MaskingUtils.class)
public interface CardMapper {

    /**
     * Converts a {@link Card} entity to a {@link CardResponse} with a masked PAN.
     *
     * <p>The PAN is masked using the {@link MaskingUtils#maskCardNumber(String)} method to ensure
     * that sensitive card information is not exposed in responses intended for general viewing,
     * such as lists of cards for administrators.</p>
     *
     * @param card The {@link Card} entity to be converted.
     * @return A {@link CardResponse} with the PAN masked.
     */
    @Mapping(target = "pan", expression = "java(MaskingUtils.maskCardNumber(card.getPan()))")
    CardResponse toMaskedResponse(Card card);

    /**
     * Converts a {@link Card} entity to a {@link CardResponse} with the full, unmasked PAN.
     *
     * <p>This method should only be used when the full card details are required, such as when
     * a card owner is viewing their own card information.</p>
     *
     * @param card The {@link Card} entity to be converted.
     * @return A {@link CardResponse} with the complete PAN.
     */
    CardResponse toFullResponse(Card card);
}
