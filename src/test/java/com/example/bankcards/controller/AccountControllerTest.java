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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    public AccountService service;

    @MockitoBean
    public AccountMapper accountMapper;

    @MockitoBean
    public CardService cardService;

    @MockitoBean
    public CardMapper cardMapper;

    @Test
    @DisplayName("GET /api/v1/accounts - возвращает страницу AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void getAccounts_returnPage() throws Exception {
        Role adminRole = new Role();
        adminRole.setId(1);
        Role userRole = new Role();
        userRole.setId(2);

        Account admin = Account.builder()
                .id(1L)
                .username("admin")
                .password("admin")
                .firstName("System")
                .lastName("Administrator")
                .phone("+7000000000")
                .email("admin@gmail.com")
                .role(adminRole)
                .bank_cards(new ArrayList<>())
                .build();

        Account user = Account.builder()
                .id(2L)
                .username("user")
                .password("user")
                .firstName("Max")
                .lastName("Crystal")
                .phone("+71234567890")
                .email("max@gmail.com")
                .role(userRole)
                .bank_cards(new ArrayList<>())
                .build();
        adminRole.setAccounts(List.of(admin));
        userRole.setAccounts(List.of(user));

        Page<Account> page = new PageImpl<>(List.of(admin, user));
        AccountResponse adminDto = new AccountResponse(1L, "admin", "System", "Administrator", "e@mail.com", "+7000");
        AccountResponse userDto = new AccountResponse(2L, "user", "first", "last", "e@mail.ru", "+7999");

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
                .andExpect(jsonPath("$.content[1].username", is("user")));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} - возвращает AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void getAccountById_returnsAccount() throws Exception {
        Role userRole = new Role();
        userRole.setId(2);
        Account user = Account.builder()
                .id(1L)
                .username("user")
                .password("user")
                .firstName("First")
                .lastName("Last")
                .phone("+7000")
                .email("e@mail.ru")
                .role(userRole)
                .bank_cards(new ArrayList<>())
                .build();

        AccountResponse dto = new AccountResponse(1L, "user", "first", "last", "e@mail.ru", "+7000");

        Mockito.when(service.getAccountById(1L)).thenReturn(user);
        Mockito.when(accountMapper.toResponse(any(Account.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/accounts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user")));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id}/cards - возвращает список карт")
    @WithMockUser("ADMIN")
    void getAccountCards_returnPage() throws Exception {
        Card c1 = new Card();
        Card c2 = new Card();
        List<Card> cards = List.of(c1, c2);

        AccountResponse accountResponse = new AccountResponse(1L, "user", "first", "last", "e@mail.ru", "+7000");
        CardStatusResponse statusResponse = new CardStatusResponse(1, null, "ACTIVE");

        CardResponse cr1 = new CardResponse(1L, "9999 9999 9999 9999", accountResponse, statusResponse, BigDecimal.ZERO);
        CardResponse cr2 = new CardResponse(1L, "0000 0000 0000 0000", accountResponse, statusResponse, BigDecimal.ZERO);

        Mockito.when(cardService.getCardsByUserId(1L)).thenReturn(cards);
        Mockito.when(cardMapper.toMaskedResponse(c1)).thenReturn(cr1);
        Mockito.when(cardMapper.toMaskedResponse(c2)).thenReturn(cr2);
        mockMvc.perform(get("/api/v1/accounts/{id}/cards", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/v1/accounts — создаёт аккаунт")
    @WithMockUser("ADMIN")
    void createAccount_returnsCreated() throws Exception {
        Role userRole = new Role();
        userRole.setId(1);
        Account entity = Account.builder()
                .id(1L)
                .username("user")
                .password("user")
                .firstName("First")
                .lastName("Last")
                .email("e@mail.ru")
                .phone("+7000")
                .role(userRole)
                .build();

        AccountResponse dto = new AccountResponse(1L, "user", "First", "Last", "e@mail.ru", "+7000");

        Mockito.when(service.createAccount(any(AccountRequest.class))).thenReturn(entity);
        Mockito.when(accountMapper.toResponse(entity)).thenReturn(dto);

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
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user")));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда удаление успешно")
    @WithMockUser(roles = "ADMIN")
    public void deleteAccount_whenDeleted_noContent() throws Exception {
        Mockito.when(service.deleteById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/v1/accounts/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда не найдено")
    @WithMockUser(roles = "ADMIN")
    void deleteAccount_whenNotFound_notFound() throws Exception {
        Mockito.when(service.deleteById(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/accounts/{id}", 2)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
