package com.example.bankcards.util.data.user;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.entity.User;

import java.util.ArrayList;

public class UserData {
    public static final User DEFAULT_ENTITY = entity().build();
    public static final UserRequest DEFAULT_REQUEST = request().build();
    public static final UserResponse DEFAULT_RESPONSE = response().build();

    private UserData() {
    }

    public static UserBuilder entity() {
        return new UserBuilder();
    }

    public static UserRequestBuilder request() {
        return new UserRequestBuilder();
    }

    public static UserResponseBuilder response() {
        return new UserResponseBuilder();
    }

    public static class UserBuilder extends BaseUserBuilder<UserBuilder> {
        private UserBuilder() {
        }

        @Override
        protected UserBuilder self() {
            return this;
        }

        public User build() {
            return User.builder()
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

    public static class UserRequestBuilder extends BaseUserBuilder<UserRequestBuilder> {
        private UserRequestBuilder() {
        }

        @Override
        protected UserRequestBuilder self() {
            return this;
        }

        public UserRequest build() {
            return new UserRequest(username, password, firstName, lastName, email, phone, roleId);
        }
    }

    public static class UserResponseBuilder extends BaseUserBuilder<UserResponseBuilder> {
        private UserResponseBuilder() {
        }

        @Override
        protected UserResponseBuilder self() {
            return this;
        }

        public UserResponse build() {
            return new UserResponse(id, username, firstName, lastName, email, phone, role);
        }
    }
}
