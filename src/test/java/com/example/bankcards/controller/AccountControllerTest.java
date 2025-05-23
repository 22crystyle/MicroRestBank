package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.CardMapperImpl;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.Account;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService service;

    @MockitoBean
    private AccountMapper mapper;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardMapperImpl cardMapper;

    @Test
    @DisplayName("GET /api/v1/accounts - возвращает страницу AccountResponse")
    @WithMockUser(roles = "ADMIN")
    void getAccounts_returnPage() throws Exception {
        Account admin = new Account();
        Account user = new Account();
        Page<Account> page = new PageImpl<>(List.of(admin, user));

        AccountResponse adminDto = new AccountResponse("admin", "System", "Administrator", "e@mail.com", "+7000");
        AccountResponse userDto = new AccountResponse("user", "first", "last", "e@mail.ru", "+7999");

        Mockito.when(service.getAllAccounts(any(PageRequest.class))).thenReturn(page);
        Mockito.when(mapper.toResponse(admin)).thenReturn(adminDto);
        Mockito.when(mapper.toResponse(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/accounts")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].name", is("admin")))
                .andExpect(jsonPath("$.content[0].email", is("e@mail.ru")))
    }
}
