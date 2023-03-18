package com.odeyalo.sonata.authentication.service.confirmation.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Contains the result about validation of the confirmation code
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ConfirmationCodeCheckResult {
    private final boolean valid;
    private String errorCode;
    private String description;

    public static final ConfirmationCodeCheckResult VALID = valid();
    public static final ConfirmationCodeCheckResult ALREADY_EXPIRED = invalid("ALREADY_EXPIRED", "The code is already expired and no longer valid");
    public static final ConfirmationCodeCheckResult INVALID_CODE = invalid("INVALID_CODE", "The code does not exist");
    public static final ConfirmationCodeCheckResult ALREADY_ACTIVATED = invalid("ALREADY_ACTIVATED", "The code is already activated.");

    public static ConfirmationCodeCheckResult valid() {
        return new ConfirmationCodeCheckResult(true);
    }

    public static ConfirmationCodeCheckResult invalid(String errorCode, String description) {
        return new ConfirmationCodeCheckResult(false, errorCode, description);
    }

    public static ConfirmationCodeCheckResult of(boolean valid, String errorCode, String description) {
        return new ConfirmationCodeCheckResult(valid, errorCode, description);
    }
}
