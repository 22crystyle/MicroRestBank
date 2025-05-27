package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.TestDataBuilders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardController.class)
public class CardControllerTest {
    @MockitoBean
    public CardMapper cardMapper;
    @MockitoBean
    public CardService cardService;
    @MockitoBean
    public CardBlockRequestService cardBlockRequestService;
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/cards - возвращает страницу AccountResponse")
    @WithMockUser("ADMIN")
    void getCards_returnPage() throws Exception {
        Page<Card> page = new PageImpl<>(List.of(new Card()));
        when(cardService.getAllCards(any())).thenReturn(page);
        when(cardMapper.toMaskedResponse(any())).thenReturn(TestDataBuilders.cardResponse().build());

        mockMvc.perform(get("/api/v1/cards")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/cards/{id} - возвращает не замаскированные данные карты пользователя")
    @WithMockUser("USER")
    void getCard_asOwner_shouldReturnFullCard() throws Exception {
        Card card = TestDataBuilders.card().build();
        when(cardService.getCard(1L)).thenReturn(card);
        when(cardService.isOwner(eq(1L), any())).thenReturn(true);
        when(cardMapper.toFullResponse(card)).thenReturn(TestDataBuilders.cardResponse().build());

        mockMvc.perform(get("/api/v1/cards/1").principal(() -> "USER")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/v1/cards - возвращает 201 при создании карты")
    @WithMockUser("ADMIN")
    void createCard_shouldReturnCreatedCard() throws Exception {
        Card card = TestDataBuilders.card().build();
        when(cardService.createCardForAccount(1L)).thenReturn(card);
        when(cardMapper.toMaskedResponse(card)).thenReturn(TestDataBuilders.cardResponse().build());

        mockMvc.perform(post("/api/v1/cards")
                        .param("userId", "1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/v1/card/transfer - перевод между картами владельца")
    @WithMockUser("USER")
    void transfer_asOwner_shouldReturnOk() throws Exception {
        when(cardService.isOwner(eq("1234"), any())).thenReturn(true);
        when(cardService.isOwner(eq("5678"), any())).thenReturn(true);
        when(cardService.transfer(eq("1234"), eq("5678"), eq(new BigDecimal("100.00")))).thenReturn(true);

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .param("from", "1234")
                        .param("to", "5678")
                        .param("amount", "100.00")
                        .principal(() -> "user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/v1/cards/transfer - возвращает 403 если карты не принадлежат пользователю")
    @WithMockUser("USER")
    void transfer_notOwner_shouldReturnForbidden() throws Exception {
        when(cardService.isOwner((Long) any(), any())).thenReturn(false);

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .param("from", "1234")
                        .param("to", "5678")
                        .param("amount", "100.00")
                        .principal(() -> "user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/v1/cards/{id}/block-request - возвращает 200 при отправке запроса")
    @WithMockUser("USER")
    void requestCardBlock_shouldSucceed() throws Exception {
        mockMvc.perform(post("/api/v1/cards/1/block-request")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(cardBlockRequestService).createBlockRequest(1L);
    }

    // Пример с авторизацией (CustomUserDetails)
    @Test
    @DisplayName("POST /api/v1/cards/{id}/block-approve - возвращает 200 при блокировке карты")
    @WithMockUser("ADMIN")
    void approveCardBlock_shouldSucceed() throws Exception {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getAccountId()).thenReturn(42L);

        CardBlockRequest mockRequest = new CardBlockRequest();
        when(cardBlockRequestService.approveBlockRequest(1L, 42L)).thenReturn(mockRequest);

        mockMvc.perform(post("/api/v1/cards/1/block-approve")
                        .with(user(userDetails))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
