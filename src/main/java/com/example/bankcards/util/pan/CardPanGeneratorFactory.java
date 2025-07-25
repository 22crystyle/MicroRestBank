package com.example.bankcards.util.pan;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CardPanGeneratorFactory {
    private final Map<String, CardPanGenerator> generators;

    public CardPanGeneratorFactory(List<CardPanGenerator> generator) {
        this.generators = generator.stream()
                .collect(Collectors.toMap(
                        gen -> gen.getClass().getSimpleName().replace("Generator", "").toLowerCase(),
                        gen -> gen
                ));
    }

    public CardPanGenerator getGenerator(String cardType) {
        return generators.getOrDefault(cardType.toLowerCase(), generators.get("mastercard"));
    }
}
