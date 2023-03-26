package com.odeyalo.sonata.authentication.common;

import com.odeyalo.sonata.authentication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public AuthenticationResult(boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    public static AuthenticationResult success(User user) {
        return new AuthenticationResult(true, user);
    }

    public static AuthenticationResult failed(ErrorDetails details) {
        return new AuthenticationResult(false, null, details);
    }

    public static final class PossibleErrors {
        // Possible errors that can be occurred
        public static final ErrorDetails EMAIL_CONFIRMATION_REQUIRED = ErrorDetails.of("email_confirmation_required", "The account exists but need to be activated", "Activate the account using confirmation code");
        public static final ErrorDetails INVALID_CREDENTIALS = ErrorDetails.of("invalid_credentials", "Invalid credentials were provided", "Check your credentials again");
    }
}
