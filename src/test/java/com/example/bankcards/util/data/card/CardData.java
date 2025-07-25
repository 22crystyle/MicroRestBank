package com.example.bankcards.util.data.card;

import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.CardStatusResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.util.data.account.AccountData;
import com.example.bankcards.util.data.card.status.CardStatusData;

import java.math.BigDecimal;
import java.time.YearMonth;

public class CardData {
    public static final Card DEFAULT_ENTITY = entity().build();
    public static final CardResponse DEFAULT_RESPONSE = response().build();

    private CardData() {
    }

    public static CardBuilder entity() {
        return new CardBuilder();
    }

    public static CardResponseBuilder response() {
        return new CardResponseBuilder();
    }

    public static class CardBuilder extends BaseCardBuilder<CardBuilder> {
        private Account owner = AccountData.DEFAULT_ENTITY;
        private YearMonth expiryDate = YearMonth.now().plusYears(4);
        private CardStatus cardStatus = CardStatusData.DEFAULT_ENTITY;
        private BigDecimal balance = BigDecimal.ZERO;

        private CardBuilder() {
        }

        public CardBuilder withOwner(Account owner) {
            this.owner = owner;
            return this;
        }

        public CardBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return self();
        }

        public CardBuilder withExpiryDate(YearMonth expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public CardBuilder withCardStatus(CardStatus cardStatus) {
            this.cardStatus = cardStatus;
            return this;
        }

        @Override
        protected CardBuilder self() {
            return this;
        }

        public Card build() {
            Card card = new Card();
            card.setId(id);
            card.setPan(cardNumber);
            card.setOwner(owner);
            card.setExpiryDate(expiryDate);
            card.setStatus(cardStatus);
            card.setBalance(balance);
            return card;
        }
    }

    public static class CardResponseBuilder extends BaseCardBuilder<CardResponseBuilder> {
        private AccountResponse owner = AccountData.DEFAULT_RESPONSE;
        private CardStatusResponse status = CardStatusData.DEFAULT_RESPONSE;
        private BigDecimal balance = BigDecimal.ZERO;
        private boolean masked = false;

        private CardResponseBuilder() {
        }

        public CardResponseBuilder withOwner(AccountResponse owner) {
            this.owner = owner;
            return this;
        }

        public CardResponseBuilder withStatus(CardStatusResponse status) {
            this.status = status;
            return this;
        }

        public CardResponseBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public CardResponseBuilder mask() {
            this.masked = true;
            return this;
        }

        @Override
        protected CardResponseBuilder self() {
            return this;
        }

        public CardResponse build() {
            String toShowNumber = masked ? mask(cardNumber) : cardNumber;
            return new CardResponse(id, toShowNumber, owner, status, balance);
        }

        private String mask(String number) {
            String digits = number.replaceAll("\\D", "");
            String last4 = digits.substring(digits.length() - 4);
            return "**** **** **** " + last4;
        }
    }
}
