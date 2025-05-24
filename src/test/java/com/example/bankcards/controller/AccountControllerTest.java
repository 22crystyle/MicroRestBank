package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.CardStatusResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.AccountService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.utils.TestDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
public class AccountControllerTest {
    @MockitoBean
    public AccountService service;
    @MockitoBean
    public AccountMapper accountMapper;
    @MockitoBean
    public CardService cardService;
    @MockitoBean
    public CardMapper cardMapper;
    @Autowired
    private MockMvc mockMvc;
    private Account userAccount;
    private Account adminAccount;
    private AccountResponse userAccountResponse;
    private AccountResponse adminAccountResponse;

    @BeforeEach
    void initData() {
        Role adminRole = TestDataBuilders.role().withId(1).withName("ROLE_ADMIN").build();
        Role userRole = TestDataBuilders.role().withId(1).withName("ROLE_USER").build();
        adminAccount = TestDataBuilders.account()
                .withId(1L)
                .withUsername("admin")
                .withRole(adminRole).build();
        adminAccountResponse = TestDataBuilders.accountResponse()
                .withId(1L)
                .withUsername("admin")
                .build();
        userAccount = TestDataBuilders.account()
                .withId(2L)
                .withUsername("user")
                .withRole(userRole).build();
        userAccountResponse = TestDataBuilders.accountResponse()
                .withId(2L)
                .withUsername("user")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/accounts - возвращает страницу AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void getAccounts_returnPage() throws Exception {
        Account admin = adminAccount;
        Account user = userAccount;

        Page<Account> page = new PageImpl<>(List.of(admin, user));
        AccountResponse adminDto = adminAccountResponse;
        AccountResponse userDto = userAccountResponse;

        Mockito.when(service.getAllAccounts(any(PageRequest.class))).thenReturn(page);
        Mockito.when(accountMapper.toResponse(admin)).thenReturn(adminDto);
        Mockito.when(accountMapper.toResponse(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/accounts")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].username", is("admin")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].username", is("user")))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} - возвращает AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void getAccountById_returnsAccount() throws Exception {
        Account admin = adminAccount;

        AccountResponse dto = adminAccountResponse;

        Mockito.when(service.getAccountById(1L)).thenReturn(admin);
        Mockito.when(accountMapper.toResponse(any(Account.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/accounts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("admin")))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id}/cards - возвращает список карт")
    @WithMockUser("ADMIN")
    void getAccountCards_returnPage() throws Exception {
        Card c1 = new Card();
        Card c2 = new Card();
        List<Card> cards = List.of(c1, c2);

        AccountResponse adminResponse = adminAccountResponse;
        CardStatusResponse statusResponse = new CardStatusResponse(1, null, "ACTIVE");

        CardResponse cr1 = new CardResponse(1L, "9999 9999 9999 9999", adminResponse, statusResponse, BigDecimal.ZERO);
        CardResponse cr2 = new CardResponse(1L, "0000 0000 0000 0000", adminResponse, statusResponse, BigDecimal.ZERO);

        Mockito.when(cardService.getCardsByUserId(2L)).thenReturn(cards);
        Mockito.when(cardMapper.toMaskedResponse(c1)).thenReturn(cr1);
        Mockito.when(cardMapper.toMaskedResponse(c2)).thenReturn(cr2);
        mockMvc.perform(get("/api/v1/accounts/{id}/cards", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/v1/accounts — создаёт и возвращает AccountResponse")
    @WithMockUser("ADMIN")
    void createAccount_returnsCreated() throws Exception {
        Account user = userAccount;
        user.setId(2L);

        AccountResponse dto = userAccountResponse;

        Mockito.when(service.createAccount(any(AccountRequest.class))).thenReturn(user);
        Mockito.when(accountMapper.toResponse(user)).thenReturn(dto);

        String jsonReq = """
                {
                    "username": "user",
                    "password": "user",
                    "firstName": "First",
                    "lastName": "Last",
                    "email": "e@mail.ru",
                    "phone": "+7000",
                    "role_id": 1
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.username", is("user")))
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда удаление успешно")
    @WithMockUser(roles = "ADMIN")
    public void deleteAccount_whenDeleted_noContent() throws Exception {
        Mockito.when(service.deleteById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/v1/accounts/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда не найдено")
    @WithMockUser(roles = "ADMIN")
    void deleteAccount_whenNotFound_notFound() throws Exception {
        Mockito.when(service.deleteById(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/accounts/{id}", 2)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
