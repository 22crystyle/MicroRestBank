package com.example.bankcards.util.user;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;

public class UserData {
    public static final User DEFAULT_ENTITY = entity().build();
    public static final UserResponse DEFAULT_RESPONSE = response().build();

    private UserData() {
    }

    public static UserBuilder entity() {
        return new UserBuilder();
    }

    public static UserResponseBuilder response() {
        return new UserResponseBuilder();
    }

    public static class UserBuilder extends BaseUserBuilder<UserBuilder> {
        private UserBuilder() {
        }

        public User build() {
            return User.builder()
                    .id(id)
                    .status(status)
                    .build();
        }

        @Override
        protected UserBuilder self() {
            return this;
        }
    }

    public static class UserResponseBuilder extends BaseUserBuilder<UserResponseBuilder> {
        private UserResponseBuilder() {
        }

        public UserResponse build() {
            return new UserResponse(id, status);
        }

        @Override
        protected UserResponseBuilder self() {
            return this;
        }
    }
}
