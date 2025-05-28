package com.example.bankcards.util;

import com.example.bankcards.annotations.FieldNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldNotEmptyValidator implements ConstraintValidator<FieldNotEmpty, Object> {
    private String field;
    private String message;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return (value != null && !value.toString().trim().isEmpty());
    }
}
