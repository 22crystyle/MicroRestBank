package com.example.bankcards.util.card.status;

import com.example.bankcards.dto.response.CardStatusResponse;
import com.example.bankcards.entity.CardStatus;

public class CardStatusData {
    public static final CardStatus DEFAULT_ENTITY = entity().build();
    public static final CardStatusResponse DEFAULT_RESPONSE = response().build();

    private CardStatusData() {
    }

    public static CardStatusBuilder entity() {
        return new CardStatusBuilder();
    }

    public static CardStatusResponseBuilder response() {
        return new CardStatusResponseBuilder();
    }

    public static class CardStatusBuilder extends BaseCardStatusBuilder<CardStatusBuilder> {
        private String description = "Card is active";

        private CardStatusBuilder() {
        }

        public CardStatusBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        protected CardStatusBuilder self() {
            return this;
        }

        public CardStatus build() {
            CardStatus cardStatus = new CardStatus();
            cardStatus.setId(id);
            cardStatus.setName(status);
            cardStatus.setDescription(description);
            return cardStatus;
        }
    }

    public static class CardStatusResponseBuilder extends BaseCardStatusBuilder<CardStatusResponseBuilder> {
        private CardStatusResponseBuilder() {
        }

        @Override
        protected CardStatusResponseBuilder self() {
            return this;
        }

        public CardStatusResponse build() {
            return new CardStatusResponse(id != null ? id : 1, null, status.name());
        }
    }
}