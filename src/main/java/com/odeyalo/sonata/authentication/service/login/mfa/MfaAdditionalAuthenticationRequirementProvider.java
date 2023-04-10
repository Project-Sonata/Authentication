package com.odeyalo.sonata.authentication.service.login.mfa;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * AdditionalAuthenticationRequirementProvider that return the
 * supported Mfa types for this user
 */
@Service
public class MfaAdditionalAuthenticationRequirementProvider implements AdditionalAuthenticationRequirementProvider {

    @Override
    public AuthenticationResult authenticate(User user) {
        UserMfaSettings userMfaSettings = user.getUserSettings().getUserMfaSettings();
        Set<UserMfaSettings.MfaType> supportedMfaTypes = userMfaSettings.getAuthorizedMfaTypes();
        return AuthenticationResult.pendingMfa(user, supportedMfaTypes);
    }

    /**
     * True only if user has enabled MFA.
     * @param user - authenticated user
     * @return - true if user has enabled mfa, false otherwise
     */
    @Override
    public boolean supports(User user) {
        return user.getUserSettings().getUserMfaSettings().isEnabled();
    }
}
