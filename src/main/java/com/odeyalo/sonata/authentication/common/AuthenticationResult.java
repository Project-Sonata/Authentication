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

    public static AuthenticationResult success(User user) {
        return new AuthenticationResult(true, user);
    }

    public static AuthenticationResult failed() {
        return new AuthenticationResult(false, null);
    }

}
