package org.restbank.service.card.util;

/**
 * Utility class for masking sensitive information, such as card numbers.
 */
public class MaskingUtils { //TODO: SonarQube
    /**
     * Masks a card number, revealing only the last four digits.
     * Replaces all but the last four digits with asterisks and adds spaces for readability.
     *
     * @param cardNumber The card number to mask.
     * @return The masked card number, or null if the input is null.
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null)
            return null;

        int length = cardNumber.length();
        if (length < 4) return cardNumber;
        String last4 = cardNumber.substring(length - 4);
        StringBuilder masked = new StringBuilder();
        for (var i = 0; i < length - 4; i++) {
            if (i % 4 == 0 && i != 0) {
                masked.append(' ');
            }
            masked.append('*');
        }
        masked.append(' ').append(last4);
        return masked.toString();
    }
}
