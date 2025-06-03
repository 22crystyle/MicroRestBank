package com.example.bankcards.annotations;

import com.example.bankcards.util.FieldNotEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {FieldNotEmptyValidator.class})
public @interface FieldNotEmpty {

    String message() default "{validation.notEmpty}";

    String field() default "Field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
