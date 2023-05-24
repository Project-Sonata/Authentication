package com.odeyalo.sonata.authentication.service.login;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.login.mfa.AdditionalAuthenticationRequirementProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Default {@link AuthenticationManager} implementation that just authenticate the users through
 * {@link UserRepository}
 */
public class DefaultAuthenticationManager implements AuthenticationManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdditionalAuthenticationRequirementProvider additionalAuthenticationRequirementProvider;

    public DefaultAuthenticationManager(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        AdditionalAuthenticationRequirementProvider additionalAuthenticationRequirementProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.additionalAuthenticationRequirementProvider = additionalAuthenticationRequirementProvider;
    }

    @Override
    public AuthenticationResult authenticate(LoginCredentials loginCredentials) {
        User user = userRepository.findUserByEmail(loginCredentials.getEmail());
        if (isUserInactive(user) || !(isPasswordMatches(loginCredentials, user))) {
            ExtendedErrorDetails details = determineError(user);
            return AuthenticationResult.failed(details);
        }
        return additionalAuthenticationRequirementProvider.authenticate(user);

    }

    private boolean isMfaEnabled(User user) {
        return user.getUserSettings().getUserMfaSettings().isEnabled();
    }

    private ExtendedErrorDetails determineError(User user) {
        return user == null || user.isActive() ? AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS : AuthenticationResult.PossibleErrors.EMAIL_CONFIRMATION_REQUIRED;
    }

    private boolean isUserInactive(User user) {
        return user == null || !user.isActive();
    }

    private boolean isPasswordMatches(LoginCredentials loginCredentials, User user) {
        return passwordEncoder.matches(loginCredentials.getPassword(), user.getPassword());
    }
}
