package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
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
    private ExtendedErrorDetails errorDetails;

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failed(ExtendedErrorDetails details) {
        return new ValidationResult(false, details);
    }
}
