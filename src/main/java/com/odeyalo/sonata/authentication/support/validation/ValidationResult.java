package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Contains info about validation
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class ValidationResult {
    private final boolean success;
    private ErrorDetails errorDetails;

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failed(ErrorDetails details) {
        return new ValidationResult(false, details);
    }
}
