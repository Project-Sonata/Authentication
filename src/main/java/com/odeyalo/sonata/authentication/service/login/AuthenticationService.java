package com.odeyalo.sonata.authentication.service.login;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.entity.User;

/**
 * Service to authenticate the user in system.
 * Service DOES NOT create any tokens
 */
public interface AuthenticationService {
    /**
     * Method to check user credentials.
     * The method DOES NOT create tokens, it just checks user credentials and return result
     * @param loginCredentials - provided credentials for login
     * @return - {@link AuthenticationResult#success(User, AuthenticationResult.Type)}
     * if credentials are valid, {@link AuthenticationResult#failed(ErrorDetails)} otherwise
     */
    AuthenticationResult authenticate(LoginCredentials loginCredentials);
}
