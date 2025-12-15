package org.restbank.libs.api.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.restbank.libs.api.annotations.FieldNotEmpty;

public class FieldNotEmptyValidator implements ConstraintValidator<FieldNotEmpty, Object> {

    @Override
    public void initialize(FieldNotEmpty constraintAnnotation) { //TODO: SonarQube

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return (value != null && !value.toString().trim().isEmpty());
    }
}
