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
import com.example.bankcards.util.TestDataBuilders;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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

    static Stream<Arguments> getAccountByIdParams() {
        return Stream.of(
                Arguments.of("ADMIN", true, 200),
                Arguments.of("USER", true, 200),
                Arguments.of("ADMIN", false, 404),
                Arguments.of("USER", false, 404),
                Arguments.of(null, true, 401),
                Arguments.of(null, false, 401)
        );
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

        when(service.getAllAccounts(any(PageRequest.class))).thenReturn(page);
        when(accountMapper.toResponse(admin)).thenReturn(adminDto);
        when(accountMapper.toResponse(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/accounts")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(admin.getId().intValue())))
                .andExpect(jsonPath("$.content[0].username", is(admin.getUsername())))
                .andExpect(jsonPath("$.content[1].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.content[1].username", is(user.getUsername())))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} - возвращает AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void getAccountById_returnsAccountResponse() throws Exception {
        Account admin = adminAccount;
        AccountResponse dto = adminAccountResponse;

        when(service.getAccountById(admin.getId())).thenReturn(admin);
        when(accountMapper.toResponse(any(Account.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/accounts/{id}", admin.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(admin.getId().intValue())))
                .andExpect(jsonPath("$.username", is(admin.getUsername())))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id}/cards - возвращает список карт")
    @WithMockUser(roles = "ADMIN")
    void getAccountCards_returnPage() throws Exception {
        Card c1 = TestDataBuilders.card().withId(1L).build();
        Card c2 = TestDataBuilders.card().withId(2L).build();
        List<Card> cards = List.of(c1, c2);

        AccountResponse adminResponse = adminAccountResponse;
        CardStatusResponse statusResponse = TestDataBuilders.cardStatusResponse().build();

        CardResponse cr1 = TestDataBuilders.cardResponse().withOwner(adminResponse).withStatus(statusResponse).build();
        CardResponse cr2 = TestDataBuilders.cardResponse().withOwner(adminResponse).withStatus(statusResponse).build();

        when(cardService.getCardsByUserId(adminResponse.id())).thenReturn(cards);
        when(cardMapper.toMaskedResponse(c1)).thenReturn(cr1);
        when(cardMapper.toMaskedResponse(c2)).thenReturn(cr2);
        mockMvc.perform(get("/api/v1/accounts/{id}/cards", adminResponse.id())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/v1/accounts — создаёт и возвращает AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void postAccount_returnsCreated() throws Exception {
        Account user = userAccount;

        AccountResponse dto = userAccountResponse;

        when(service.createAccount(any(AccountRequest.class))).thenReturn(user);
        when(accountMapper.toResponse(user)).thenReturn(dto);

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
                        .content(jsonReq)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userAccount.getId().intValue())))
                .andExpect(jsonPath("$.username", is(userAccount.getUsername())))
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда удаление успешно")
    @WithMockUser(roles = "ADMIN")
    public void deleteAccount_whenDeleted_noContent() throws Exception {
        when(service.deleteById(userAccount.getId())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/accounts/{id}", userAccount.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда не найдено")
    @WithMockUser(roles = "ADMIN")
    void deleteAccount_whenNotFound_notFound() throws Exception {
        when(service.deleteById(userAccount.getId())).thenReturn(false);

        mockMvc.perform(delete("/api/v1/accounts/{id}", userAccount.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @ParameterizedTest(name = "[{index}] role={0}, exists={1} => status={2}")
    @MethodSource("getAccountByIdParams")
    void getAccountId_withRolesAndExistence(String role, boolean exists, int expectedStatus) throws Exception {
        Long id = 99L;

        if (exists) {
            when(service.getAccountById(id)).thenReturn(adminAccount);
            when(accountMapper.toResponse(adminAccount)).thenReturn(adminAccountResponse);
        } else {
            when(service.getAccountById(id)).thenThrow(new EntityNotFoundException("Account not found"));
        }

        RequestPostProcessor auth = role != null
                ? user("test").roles(role.replace("ROLE_", ""))
                : anonymous();

        mockMvc.perform(get("/api/v1/accounts/{id}", id)
                        .with(auth)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }
}
