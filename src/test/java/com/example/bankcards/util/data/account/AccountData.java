package com.example.bankcards.util.data.account;

import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.Account;

import java.util.ArrayList;

public class AccountData {
    public static final Account DEFAULT_ENTITY = entity().build();
    public static final AccountRequest DEFAULT_REQUEST = request().build();
    public static final AccountResponse DEFAULT_RESPONSE = response().build();

    private AccountData() {
    }

    public static AccountBuilder entity() {
        return new AccountBuilder();
    }

    public static AccountRequestBuilder request() {
        return new AccountRequestBuilder();
    }

    public static AccountResponseBuilder response() {
        return new AccountResponseBuilder();
    }

    public static class AccountBuilder extends BaseAccountBuilder<AccountBuilder> {
        private AccountBuilder() {
        }

        @Override
        protected AccountBuilder self() {
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

    public static class AccountRequestBuilder extends BaseAccountBuilder<AccountRequestBuilder> {
        private AccountRequestBuilder() {
        }

        @Override
        protected AccountRequestBuilder self() {
            return this;
        }

        public AccountRequest build() {
            return new AccountRequest(username, password, firstName, lastName, email, phone, roleId);
        }
    }

    public static class AccountResponseBuilder extends BaseAccountBuilder<AccountResponseBuilder> {
        private AccountResponseBuilder() {
        }

        @Override
        protected AccountResponseBuilder self() {
            return this;
        }

        public AccountResponse build() {
            return new AccountResponse(id, username, firstName, lastName, email, phone, role);
        }
    }
}
