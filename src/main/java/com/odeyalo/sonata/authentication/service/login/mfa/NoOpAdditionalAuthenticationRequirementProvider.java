package com.odeyalo.sonata.authentication.service.login.mfa;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.entity.User;
import org.springframework.stereotype.Service;

/**
 * AdditionalAuthenticationService impl class, that always returns login_completed
 */
@Service
public class NoOpAdditionalAuthenticationRequirementProvider implements AdditionalAuthenticationRequirementProvider {

    @Override
    public AuthenticationResult authenticate(User user) {
        return AuthenticationResult.success(user, AuthenticationResult.Type.LOGIN_COMPLETED);
    }

    /**
     * True only if user has disabled MFA.
     * @param user - authenticated user
     * @return - true if user has disabled mfa, false otherwise
     */
    @Override
    public boolean supports(User user) {
        return !user.getUserSettings().getUserMfaSettings().isEnabled();
    }
}
