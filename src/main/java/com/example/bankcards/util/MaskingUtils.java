package com.example.bankcards.util;

public class MaskingUtils {
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
