package com.odeyalo.sonata.authentication.service.confirmation.support;

import com.odeyalo.sonata.authentication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

/**
 * Contains the result about validation of the confirmation code
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ConfirmationCodeCheckResult {
    private final boolean valid;
    // User that has confirmed the confirmation code, if valid - user entity, if code is invalid - null
    private User user;
    private String errorCode;
    private String description;

    public static final ConfirmationCodeCheckResult ALREADY_EXPIRED = invalid("ALREADY_EXPIRED", "The code is already expired and no longer valid");
    public static final ConfirmationCodeCheckResult INVALID_CODE = invalid("INVALID_CODE", "The code does not exist");
    public static final ConfirmationCodeCheckResult ALREADY_ACTIVATED = invalid("ALREADY_ACTIVATED", "The code is already activated.");

    public static ConfirmationCodeCheckResult valid(User user) {
        Assert.notNull(user, "If code is valid, then user must be not null!");
        return new ConfirmationCodeCheckResult(true, user, null, "Code is valid");
    }

    public static ConfirmationCodeCheckResult invalid(String errorCode, String description) {
        return new ConfirmationCodeCheckResult(false, null, errorCode, description);
    }

    public static ConfirmationCodeCheckResult of(boolean valid, String errorCode, String description) {
        return new ConfirmationCodeCheckResult(valid, null, errorCode, description);
    }
}
