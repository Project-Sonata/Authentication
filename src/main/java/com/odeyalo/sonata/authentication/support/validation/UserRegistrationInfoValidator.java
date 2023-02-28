package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;

/**
 * Validate the {@link UserRegistrationInfo} by some conditions
 */
public interface UserRegistrationInfoValidator {
    /**
     * Validate the given {@link UserRegistrationInfo}
     * @param info - info to validate
     * @return - {@link ValidationResult#success()} if info is valid, {@link ValidationResult#failed(ErrorDetails)} ()} if the info is invalid.
     */
    ValidationResult validateInfo(UserRegistrationInfo info);
}
