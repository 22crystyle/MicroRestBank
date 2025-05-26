package com.example.bankcards.controller;

import com.example.bankcards.Main;
import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.service.AccountService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.TestDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = Main.class,
        properties = "spring.liquibase.enabled=false"
)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
public class AccountControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private AccountController accountController;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CardService cardService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CardStatusRepository cardStatusRepository;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        cardStatusRepository.deleteAll();

        Role adminRole = TestDataBuilders.role()
                .withName("ADMIN")
                .build();

        Role userRole = TestDataBuilders.role()
                .withName("USER")
                .build();

        CardStatus cardStatus = TestDataBuilders.cardStatus().build();

        Account admin = TestDataBuilders.account()
                .withUsername("admin")
                .withPassword("pass")
                .withRole(adminRole)
                .build();

        Account user = TestDataBuilders.account()
                .withUsername("user")
                .withPassword("pass")
                .withRole(userRole)
                .build();

        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        cardStatusRepository.save(cardStatus);
        accountRepository.save(admin);
        accountRepository.save(user);


        Card card = TestDataBuilders.card()
                .withOwner(user)
                .withCardNumber("1234123412341234")
                .withCardStatus(cardStatus)
                .build();
        cardRepository.save(card);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAccounts_returnsOkAndPage() throws Exception {
        mockMvc.perform(get("/api/v1/accounts")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAccountById_returnsOk() throws Exception {
        Account admin = accountRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(get("/api/v1/accounts/{id}", admin.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andDo(print());
    }
}
