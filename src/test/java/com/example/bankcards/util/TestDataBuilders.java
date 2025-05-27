package com.example.bankcards.util;

import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.CardStatusResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public final class TestDataBuilders {

    private TestDataBuilders() {
    }

    // === Account Builder ===
    public static AccountBuilder account() {
        return new AccountBuilder();
    }

    // === Role Builder ===
    public static RoleBuilder role() {
        return new RoleBuilder();
    }

    // === AccountResponseBuilder ===
    public static AccountResponseBuilder accountResponse() {
        return new AccountResponseBuilder();
    }

    // === AccountRequestBuilder ===
    public static AccountRequestBuilder accountRequest() {
        return new AccountRequestBuilder();
    }

    // === Card Builder ===
    public static CardBuilder card() {
        return new CardBuilder();
    }

    // === CardStatus Builder
    public static CardStatusBuilder cardStatus() {
        return new CardStatusBuilder();
    }

    // === CardResponse Builder ===
    public static CardResponseBuilder cardResponse() {
        return new CardResponseBuilder();
    }

    // === CardStatusResponse Builder ===
    public static CardStatusResponseBuilder cardStatusResponse() {
        return new CardStatusResponseBuilder();
    }

    public static class AccountBuilder {
        private Long id;
        private String username = "user";
        private String password = "pass";
        private String firstName = "First";
        private String lastName = "Last";
        private String email = "user@example.com";
        private String phone = "+70000000000";
        private Role role = TestDataBuilders.role().build();

        private AccountBuilder() {
        }

        public AccountBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AccountBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public AccountBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public AccountBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AccountBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AccountBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public AccountBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public AccountBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public Account build() {
            return Account.builder()
                    .id(id)
                    .username(username)
                    .password(password)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phone(phone)
                    .role(role)
                    .bank_cards(new ArrayList<>())
                    .build();
        }
    }

    public static class RoleBuilder {
        private Integer id;
        private String name = "USER";

        private RoleBuilder() {
        }

        public RoleBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public RoleBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Role build() {
            Role r = new Role();
            r.setId(id);
            r.setName(name);
            return r;
        }
    }

    public static class AccountResponseBuilder {
        private Long id = 1L;
        private String username = "user";
        private String firstName = "First";
        private String lastName = "Last";
        private String email = "user@example.com";
        private String phone = "+70000000000";

        private AccountResponseBuilder() {
        }

        public AccountResponseBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AccountResponseBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public AccountResponseBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AccountResponseBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AccountResponseBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public AccountResponseBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public AccountResponse build() {
            return new AccountResponse(id, username, firstName, lastName, email, phone);
        }
    }

    public static class AccountRequestBuilder {
        private String username = "user";
        private String password = "pass";
        private String firstName = "firstName";
        private String lastName = "lastName";
        private String email = "e@mail.com";
        private String phone = "+71111155555";
        private Integer role_id = 1;

        AccountRequestBuilder() {
        }

        public AccountRequestBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public AccountRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public AccountRequestBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AccountRequestBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AccountRequestBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public AccountRequestBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public AccountRequestBuilder withRoleId(Integer role_id) {
            this.role_id = role_id;
            return this;
        }

        public AccountRequest build() {
            return new AccountRequest(this.username, this.password, this.firstName, this.lastName, this.email, this.phone, this.role_id);
        }
    }

    public static class CardBuilder {
        private Long id;
        private String cardNumber;
        private Account owner;
        private YearMonth expiryDate = YearMonth.now().plusYears(4);
        private CardStatus cardStatus;

        private CardBuilder() {
        }

        public CardBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CardBuilder withCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public CardBuilder withOwner(Account owner) {
            this.owner = owner;
            return this;
        }

        public CardBuilder withExpiryDate(YearMonth expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public CardBuilder withCardStatus(CardStatus cardStatus) {
            this.cardStatus = cardStatus;
            return this;
        }

        public Card build() {
            Card card = new Card();
            card.setId(id);
            card.setCardNumber(cardNumber);
            card.setOwner(owner);
            card.setExpiryDate(expiryDate);
            card.setStatus(cardStatus);
            return card;
        }
    }

    public static class CardStatusBuilder {
        private Integer id;
        private String name = "ACTIVE";
        private String description = "Card is active";
        private List<Card> cards = new ArrayList<>();

        private CardStatusBuilder() {
        }

        public CardStatusBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public CardStatusBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CardStatusBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CardStatusBuilder withCards(List<Card> cards) {
            this.cards = cards;
            return this;
        }

        public CardStatus build() {
            CardStatus cardStatus = new CardStatus();
            cardStatus.setId(id);
            cardStatus.setName(name);
            cardStatus.setDescription(description);
            cardStatus.setCards(cards);
            return cardStatus;
        }
    }

    public static class CardResponseBuilder {
        private Long id = 1L;
        private String number = "1234 1234 1234 1234";
        private AccountResponse owner = accountResponse().build();
        private CardStatusResponse status = cardStatusResponse().build();
        private BigDecimal balance = BigDecimal.ZERO;
        private boolean masked = false;

        private CardResponseBuilder() {
        }

        public CardResponseBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CardResponseBuilder withNumber(String number) {
            this.number = number;
            return this;
        }

        public CardResponseBuilder mask() {
            this.masked = true;
            return this;
        }

        public CardResponseBuilder withOwner(AccountResponse owner) {
            this.owner = owner;
            return this;
        }

        public CardResponseBuilder withStatus(CardStatusResponse status) {
            this.status = status;
            return this;
        }

        public CardResponseBuilder withBalance(BigDecimal bal) {
            this.balance = bal;
            return this;
        }

        public CardResponse build() {
            String toShowNumber = masked
                    ? mask(number)
                    : number;
            return new CardResponse(id, toShowNumber, owner, status, balance);
        }

        private String mask(String number) {
            String digits = number.replaceAll("\\D", "");
            String last4 = digits.substring(digits.length() - 4);
            return "**** **** **** " + last4;
        }
    }

    public static class CardStatusResponseBuilder {
        private int statusId = 1;
        private String status = "ACTIVE";

        private CardStatusResponseBuilder() {
        }

        public CardStatusResponseBuilder withStatusId(int id) {
            this.statusId = id;
            return this;
        }

        public CardStatusResponseBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public CardStatusResponse build() {
            return new CardStatusResponse(statusId, null, status);
        }
    }

}
