package org.restbank.service.card.util.pan;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Implements {@link CardPanGenerator} to generate valid Mastercard Payment Account Numbers (PANs).
 * Uses Luhn algorithm for check digit calculation.
 */
@Component
public class MastercardGenerator implements CardPanGenerator {
    private static final String[] MastercardPrefixes = {
            "51", "52", "53", "54", "55",
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229",
            "223", "224", "225", "226", "227", "228", "229",
            "23", "24", "25", "26",
            "270", "271", "2720"
    };

    private static final Random random = new Random();

    /**
     * Calculates the Luhn check digit for a given card number string.
     *
     * @param number The card number string without the check digit.
     * @return The calculated Luhn check digit.
     */
    private static int getLuhnCheckDigit(String number) {
        int sum = 0;
        boolean shouldDouble = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (shouldDouble) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            shouldDouble = !shouldDouble;
        }
        return (10 - (sum % 10)) % 10;
    }

    /**
     * Generates a 16-digit Mastercard PAN, including a valid Luhn check digit.
     *
     * @return A string representing the generated Mastercard PAN.
     */
    @Override
    public String generateCardPan() {
        String prefix = MastercardPrefixes[random.nextInt(MastercardPrefixes.length)];
        int length = 16;
        int numberLength = length - (prefix.length() + 1);
        StringBuilder builder = new StringBuilder(prefix);

        for (int i = 0; i < numberLength; i++) {
            builder.append(random.nextInt(10));
        }

        String numberWithoutCheckDigit = builder.toString();
        int checkDigit = getLuhnCheckDigit(numberWithoutCheckDigit);
        builder.append(checkDigit);

        return builder.toString();
    }
}
