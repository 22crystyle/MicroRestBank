package com.example.bankcards.controller;

import com.example.bankcards.Main;
import com.example.bankcards.util.data.card.CardData;
import com.example.bankcards.util.data.card.status.CardStatusData;
import com.example.bankcards.util.data.user.UserData;
import com.example.bankcards.util.data.user.role.RoleData;
import com.example.shared.entity.Card;
import com.example.shared.entity.CardStatus;
import com.example.shared.entity.Role;
import com.example.shared.entity.User;
import com.example.shared.repository.CardRepository;
import com.example.shared.repository.CardStatusRepository;
import com.example.shared.repository.RoleRepository;
import com.example.shared.repository.UserRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class UserControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CardStatusRepository cardStatusRepository;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
        cardStatusRepository.deleteAll();

        Role adminRole = RoleData.role()
                .withName("ADMIN")
                .build();

        Role userRole = RoleData.role()
                .withName("USER")
                .build();

        CardStatus cardStatus = CardStatusData.entity().build();

        User admin = UserData.entity()
                .withUsername("admin")
                .withPassword("pass")
                .withRole(adminRole)
                .build();

        User user = UserData.entity()
                .withUsername("user")
                .withPassword("pass")
                .withRole(userRole)
                .build();

        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        cardStatusRepository.save(cardStatus);
        userRepository.save(admin);
        userRepository.save(user);


        Card card = CardData.entity()
                .withOwner(user)
                .withPan("1234123412341234")
                .withCardStatus(cardStatus)
                .build();
        cardRepository.save(card);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPageOfAccounts_returnsOkAndPage() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_returnsOk() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(get("/api/v1/users/{id}", admin.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAccountCard_returnsOkAndList() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(get("/api/v1/users/{id}/cards", admin.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_returnsCreated() throws Exception {
        Role role = roleRepository.getRoleByName("USER").orElseThrow();
        String json = """
                {
                "username": "newuser",
                "firstName": "John",
                "lastName": "Doe",
                "password": "pass",
                "roleId": "%d"
                }
                """.formatted(role.getId());

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_returnsNoContent() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(delete("/api/v1/users/{id}", admin.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_missing_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{id}", 999)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
