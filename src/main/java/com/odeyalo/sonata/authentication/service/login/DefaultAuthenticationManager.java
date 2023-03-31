package com.odeyalo.sonata.authentication.service.login;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default {@link AuthenticationManager} implementation that just authenticate the users through
 * {@link UserRepository}
 */
@Service
public class DefaultAuthenticationManager implements AuthenticationManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultAuthenticationManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResult authenticate(LoginCredentials loginCredentials) {
        User user = userRepository.findUserByEmail(loginCredentials.getEmail());
        if (isUserInactive(user) || !(isPasswordMatches(loginCredentials, user))) {
            ErrorDetails details = determineError(user);
            return AuthenticationResult.failed(details);
        }
        return AuthenticationResult.success(user);
    }

    private ErrorDetails determineError(User user) {
        return user == null || user.isActive() ? AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS : AuthenticationResult.PossibleErrors.EMAIL_CONFIRMATION_REQUIRED;
    }

    private boolean isUserInactive(User user) {
        return user == null || !user.isActive();
    }

    private boolean isPasswordMatches(LoginCredentials loginCredentials, User user) {
        return passwordEncoder.matches(loginCredentials.getPassword(), user.getPassword());
    }
}
