package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;

/**
 * Validate the {@link AdvancedUserRegistrationInfo} by some conditions
 */
public interface UserRegistrationInfoValidator {
    /**
     * Validate the given {@link AdvancedUserRegistrationInfo}
     * @param info - info to validate
     * @return - {@link ValidationResult#success()} if info is valid, {@link ValidationResult#failed(ExtendedErrorDetails)} ()} if the info is invalid.
     */
    ValidationResult validateInfo(AdvancedUserRegistrationInfo info);
}
