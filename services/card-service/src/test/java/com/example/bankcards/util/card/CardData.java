package com.example.bankcards.util.card;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.CardStatusResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.card.status.CardStatusData;
import com.example.bankcards.util.user.UserData;

import java.time.YearMonth;

public class CardData {
    public static final Card DEFAULT_ENTITY = entity().build();
    public static final CardResponse DEFAULT_RESPONSE = response().build();

    private CardData() {
    }

    public static CardBuilder entity() {
        return new CardBuilder();
    }

    private static CardResponseBuilder response() {
        return new CardResponseBuilder();
    }

    public static final class CardBuilder extends BaseCardBuilder<CardBuilder> {
        private User owner = UserData.DEFAULT_ENTITY;
        private YearMonth expiryDate = YearMonth.now().plusYears(4);
        private CardStatus cardStatus = CardStatusData.DEFAULT_ENTITY;

        private CardBuilder() {
        }

        public CardBuilder withOwner(User owner) {
            this.owner = owner;
            return self();
        }

        public CardBuilder withExpiryDate(YearMonth yearMonth) {
            this.expiryDate = yearMonth;
            return self();
        }

        public CardBuilder withCardStatus(CardStatus status) {
            this.cardStatus = status;
            return self();
        }

        public Card build() {
            return Card.builder()
                    .id(id)
                    .pan(pan)
                    .user(owner)
                    .expiryDate(expiryDate)
                    .status(cardStatus)
                    .balance(balance)
                    .build();
        }

        @Override
        protected CardBuilder self() {
            return this;
        }
    }

    public static final class CardResponseBuilder extends BaseCardBuilder<CardResponseBuilder> {
        private UserResponse owner = UserData.DEFAULT_RESPONSE;
        private CardStatusResponse status = CardStatusData.DEFAULT_RESPONSE;

        private CardResponseBuilder() {
        }

        public CardResponseBuilder withOwner(UserResponse owner) {
            this.owner = owner;
            return self();
        }

        public CardResponseBuilder withStatus(CardStatusResponse status) {
            this.status = status;
            return self();
        }

        public CardResponse build() {
            return new CardResponse(id, pan, owner, status, balance);
        }

        @Override
        protected CardResponseBuilder self() {
            return this;
        }
    }
}
