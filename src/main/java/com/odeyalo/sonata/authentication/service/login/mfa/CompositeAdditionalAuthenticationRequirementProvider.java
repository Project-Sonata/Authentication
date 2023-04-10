package com.odeyalo.sonata.authentication.service.login.mfa;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.entity.User;

import java.util.List;

/**
 * Simple composite that used as entrypoint for all AdditionalAuthenticationRequirementProvider
 */
public class CompositeAdditionalAuthenticationRequirementProvider implements AdditionalAuthenticationRequirementProvider {
    private final List<AdditionalAuthenticationRequirementProvider> providers;

    public CompositeAdditionalAuthenticationRequirementProvider(List<AdditionalAuthenticationRequirementProvider> providers) {
        this.providers = providers;
    }

    @Override
    public AuthenticationResult authenticate(User user) {
        for (AdditionalAuthenticationRequirementProvider provider : providers) {
            if (provider.supports(user)) {
                return provider.authenticate(user);
            }
        }
        throw new IllegalArgumentException("Failed to resolve a AdditionalAuthenticationRequirementProvider that supports: " + user);
    }

    /**
     * Always true since this class works as composite
     * @param user - authenticated user
     * @return - always true
     */
    @Override
    public boolean supports(User user) {
        return true;
    }
}
