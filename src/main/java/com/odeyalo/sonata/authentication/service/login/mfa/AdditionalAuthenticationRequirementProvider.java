package com.odeyalo.sonata.authentication.service.login.mfa;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.entity.User;

/**
 * Interface that add additional authentication requirement layer for user.
 * For example, this service can be used to create MfaAdditionalAuthenticationService that will require additional confirmation using MFA.
 * If AdditionalAuthenticationService should do nothing, then EmptyAdditionalAuthenticationService can be used
 *
 * Note: This interface SHOULD BE invoked IF AND ONLY IF the user's credentials are correct.
 */
public interface AdditionalAuthenticationRequirementProvider {
    /**
     * Check if user has enabled additional confirmations during login, if so, then returns, for example,
     * {@link AuthenticationResult#success(User, AuthenticationResult.Type)} with user and  AuthenticationResult.Type.PENDING_MFA
     * @param user - user that was logged-in with correct credentials
     * @return - AuthenticationResult that requires additional confirmation.
     * If additional login confirmation wasn't configured for user, then AuthenticationResult.Type.LOGIN_COMPLETED
     */
    AuthenticationResult authenticate(User user);

    /**
     * True if this provider supports additional authentication or not.
     * Note: ONLY THE FIRST implementation will be picked, other implementations will be ignored
     * @param user - authenticated user
     * @return - true if this provider supports additional authentication for user, false otherwise
     * @see NoOpAdditionalAuthenticationRequirementProvider#supports(User)
     * @see MfaAdditionalAuthenticationRequirementProvider#supports(User)
     */
    boolean supports(User user);
}
