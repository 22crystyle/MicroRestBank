package com.example.bankcards.service;

import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.pan.CardPanGenerator;
import com.example.bankcards.util.pan.CardPanGeneratorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardStatusRepository cardStatusRepository;

    @Mock
    private CardPanGeneratorFactory cardPanGeneratorFactory;

    @Mock
    private CardPanGenerator cardPanGenerator;

    @InjectMocks
    private CardService cardService;

    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        when(cardPanGeneratorFactory.getGenerator(anyString())).thenReturn(cardPanGenerator);
    }


}
