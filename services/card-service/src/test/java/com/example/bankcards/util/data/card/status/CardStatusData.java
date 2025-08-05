package com.example.bankcards.util.data.card.status;

import com.example.bankcards.dto.response.CardStatusResponse;
import com.example.entity.Card;
import com.example.entity.CardStatus;

import java.util.ArrayList;
import java.util.List;

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
        private List<Card> cards = new ArrayList<>();

        private CardStatusBuilder() {
        }

        public CardStatusBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CardStatusBuilder withCards(List<Card> cards) {
            this.cards = cards;
            return this;
        }

        @Override
        protected CardStatusBuilder self() {
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

    public static class CardStatusResponseBuilder extends BaseCardStatusBuilder<CardStatusResponseBuilder> {
        private CardStatusResponseBuilder() {
        }

        @Override
        protected CardStatusResponseBuilder self() {
            return this;
        }

        public CardStatusResponse build() {
            return new CardStatusResponse(id != null ? id : 1, null, name);
        }
    }
}
