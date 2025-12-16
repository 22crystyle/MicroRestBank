package org.restbank.libs.api.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = false)
public class ValidationErrorResponse extends RestErrorResponse {
    private final Map<String, String> errors;

    public ValidationErrorResponse(String source, String code, String message, Map<String, String> errors) {
        super(source, code, message);
        this.errors = errors;
    }
}
