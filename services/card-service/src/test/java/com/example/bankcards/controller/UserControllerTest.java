package com.example.bankcards.controller;

import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.data.card.CardData;
import com.example.bankcards.util.data.card.status.CardStatusData;
import com.example.bankcards.util.data.user.UserData;
import com.example.bankcards.util.data.user.role.RoleData;
import com.example.shared.dto.CardMapper;
import com.example.shared.dto.UserMapper;
import com.example.shared.dto.request.UserRequest;
import com.example.shared.dto.response.CardResponse;
import com.example.shared.dto.response.CardStatusResponse;
import com.example.shared.dto.response.UserResponse;
import com.example.shared.entity.Card;
import com.example.shared.entity.Role;
import com.example.shared.entity.User;
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
import org.springframework.security.core.userdetails.UserDetailsService;
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

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockitoBean
    private UserService service;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private CardService cardService;
    @MockitoBean
    private CardMapper cardMapper;
    @MockitoBean(name = "customUserDetailsService")
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;

    private User user;
    private User admin;
    private UserResponse userResponse;
    private UserResponse adminResponse;
    private String token;

    static Stream<Arguments> getUserByIdParams() {
        return Stream.of(
                Arguments.of("ADMIN", true, 200),
                Arguments.of("USER", true, 200),
                Arguments.of("ADMIN", false, 404),
                Arguments.of("USER", false, 404),
                Arguments.of(null, true, 401),
                Arguments.of(null, false, 401)
        );
    }

    @BeforeEach
    void initData() {
        Role adminRole = RoleData.role().withId(1).withName("ROLE_ADMIN").build();
        Role userRole = RoleData.role().withId(2).withName("ROLE_USER").build();
        admin = UserData.entity()
                .withId(1L)
                .withUsername("admin")
                .withRole(adminRole).build();
        adminResponse = UserData.response()
                .withId(1L)
                .withUsername("admin")
                .build();
        user = UserData.entity()
                .withId(2L)
                .withUsername("user")
                .withRole(userRole).build();
        userResponse = UserData.response()
                .withId(2L)
                .withUsername("user")
                .build();

        String userToken = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());
        when(jwtUtil.validateToken(userToken, user.getUsername())).thenReturn(true);
        when(jwtUtil.extractUsername(userToken)).thenReturn("user");


        String adminToken = jwtUtil.generateToken(admin.getUsername(), admin.getRole().getName());
        when(jwtUtil.validateToken(adminToken, admin.getUsername())).thenReturn(true);
        when(jwtUtil.extractUsername(adminToken)).thenReturn("admin");
    }

    @Test
    @DisplayName("GET /api/v1/accounts - возвращает страницу AccountResponse")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getPageOfAccounts_returnPage() throws Exception {
        User admin = this.admin;
        User user = this.user;

        Page<User> page = new PageImpl<>(List.of(admin, user));
        UserResponse adminDto = adminResponse;
        UserResponse userDto = userResponse;

        when(service.getPage(any(PageRequest.class))).thenReturn(page);
        when(userMapper.toResponse(admin)).thenReturn(adminDto);
        when(userMapper.toResponse(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users")
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
    void getUserById_returnsUserResponse() throws Exception {
        User admin = this.admin;
        UserResponse dto = adminResponse;

        when(service.getUserById(admin.getId())).thenReturn(admin);
        when(userMapper.toResponse(any(User.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/{id}", admin.getId())
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
        Card c1 = CardData.entity().withId(1L).build();
        Card c2 = CardData.entity().withId(2L).build();
        List<Card> cards = List.of(c1, c2);

        UserResponse adminResponse = this.adminResponse;
        CardStatusResponse statusResponse = CardStatusData.DEFAULT_RESPONSE;

        CardResponse cr1 = CardData.response().withOwner(adminResponse).withStatus(statusResponse).build();
        CardResponse cr2 = CardData.response().withOwner(adminResponse).withStatus(statusResponse).build();

        when(cardService.getByOwner(adminResponse.id())).thenReturn(cards);
        when(cardMapper.toMaskedResponse(c1)).thenReturn(cr1);
        when(cardMapper.toMaskedResponse(c2)).thenReturn(cr2);
        mockMvc.perform(get("/api/v1/users/{id}/cards", adminResponse.id())
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
        User user = this.user;

        UserResponse dto = userResponse;

        when(service.createUser(any(UserRequest.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(dto);

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

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .content(jsonReq)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(this.user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(this.user.getUsername())))
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда удаление успешно")
    @WithMockUser(roles = "ADMIN")
    public void deleteUser_whenDeleted_noContent() throws Exception {
        when(service.deleteById(user.getId())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} — когда не найдено")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_whenNotFound_notFound() throws Exception {
        when(service.deleteById(user.getId())).thenReturn(false);

        mockMvc.perform(delete("/api/v1/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @ParameterizedTest(name = "[{index}] role={0}, exists={1} => status={2}")
    @MethodSource("getUserByIdParams")
    void getAccountId_withRolesAndExistence(String role, boolean exists, int expectedStatus) throws Exception {
        Long id = 99L;

        if (exists) {
            when(service.getUserById(id)).thenReturn(admin);
            when(userMapper.toResponse(admin)).thenReturn(adminResponse);
        } else {
            when(service.getUserById(id)).thenThrow(new UserNotFoundException(id));
        }

        RequestPostProcessor auth = role != null
                ? user("test").roles(role.replace("ROLE_", ""))
                : anonymous();

        mockMvc.perform(get("/api/v1/users/{id}", id)
                        .with(auth)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }
}
