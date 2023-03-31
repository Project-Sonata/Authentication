package com.odeyalo.sonata.authentication.service.login;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.entity.User;

/**
 * Manager to authenticate the user in system.
 * Manager DOES NOT create any tokens
 */
public interface AuthenticationManager {
    /**
     * Method to check user credentials.
     * The method DOES NOT create tokens, it just checks user credentials and return result
     * @param loginCredentials - provided credentials for login
     * @return - {@link AuthenticationResult#success(User)} ()} if credentials are valid, {@link AuthenticationResult#failed()} otherwise
     */
    AuthenticationResult authenticate(LoginCredentials loginCredentials);
}
