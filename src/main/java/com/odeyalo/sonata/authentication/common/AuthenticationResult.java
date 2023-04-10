package com.odeyalo.sonata.authentication.common;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Result of the authentication, if everything is okay, then authenticated user will be returned
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResult {
    private boolean success;
    private User user;
    // Used if the application cannot perform authentication
    private ErrorDetails errorDetails;
    private Type type;
    private Set<UserMfaSettings.MfaType> supportedMfaTypes;

    public AuthenticationResult(boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    public static AuthenticationResult pendingMfa(User user, Set<UserMfaSettings.MfaType> supportedMfaTypes) {
        return new AuthenticationResult(true, user, null, Type.PENDING_MFA, supportedMfaTypes);
    }

    public static AuthenticationResult success(User user, Type type) {
        return new AuthenticationResult(true, user, null, type, user.getUserSettings().getUserMfaSettings().getAuthorizedMfaTypes());
    }

    public static AuthenticationResult failed(ErrorDetails details) {
        return new AuthenticationResult(false, null, details, Type.FAILED, null);
    }

    /**
     * More detailed information about authentication status
     */
    @Getter
    public enum Type {
        PENDING_MFA("pending_mfa"),
        LOGIN_COMPLETED("login_completed"),
        // If authentication cannot be performed
        FAILED("failed");
        private final String name;

        Type(String name) {
            this.name = name;
        }
    }

    public static final class PossibleErrors {
        // Possible errors that can be occurred
        public static final ErrorDetails EMAIL_CONFIRMATION_REQUIRED = ErrorDetails.of("email_confirmation_required", "The account exists but need to be activated", "Activate the account using confirmation code");
        public static final ErrorDetails INVALID_CREDENTIALS = ErrorDetails.of("invalid_credentials", "Invalid credentials were provided", "Check your credentials again");
    }
}
