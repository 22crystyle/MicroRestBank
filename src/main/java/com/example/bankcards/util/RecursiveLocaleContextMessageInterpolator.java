package com.example.bankcards.util;

import jakarta.validation.MessageInterpolator;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveLocaleContextMessageInterpolator extends AbstractMessageInterpolator {
    private static final Pattern PATTERN_PLACEHOLDER = Pattern.compile("\\{([^}]+)}");

    private final MessageInterpolator interpolator;

    public RecursiveLocaleContextMessageInterpolator(ResourceBundleMessageInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    protected String interpolate(Context context, Locale locale, String message) {
        int level = 0;
        while (containsPlaceholder(message) && level++ < 2) {
            message = this.interpolator.interpolate(message, context, locale);
        }
        return message;
    }

    private boolean containsPlaceholder(String message) {
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(message);
        return matcher.find();
    }
}
