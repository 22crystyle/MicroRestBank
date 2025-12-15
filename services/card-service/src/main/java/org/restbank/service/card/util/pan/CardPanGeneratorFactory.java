package org.restbank.service.card.util.pan;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory class for providing {@link CardPanGenerator} instances based on card type.
 */
@Component
public class CardPanGeneratorFactory {
    private final Map<String, CardPanGenerator> generators;

    /**
     * Constructs a new CardPanGeneratorFactory.
     *
     * @param generator A list of available CardPanGenerator implementations.
     */
    public CardPanGeneratorFactory(List<CardPanGenerator> generator) {
        this.generators = generator.stream()
                .collect(Collectors.toMap(
                        gen -> gen.getClass().getSimpleName().replace("Generator", "").toLowerCase(),
                        gen -> gen
                ));
    }

    /**
     * Retrieves a {@link CardPanGenerator} for the specified card type.
     * Defaults to a Mastercard generator if the specified card type is not found.
     *
     * @param cardType The type of card for which to get a generator (e.g., "mastercard").
     * @return A CardPanGenerator instance.
     */
    public CardPanGenerator getGenerator(String cardType) {
        return generators.getOrDefault(cardType.toLowerCase(), generators.get("mastercard"));
    }
}
