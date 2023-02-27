package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

/**
 * Simple {@link UserRegistrationInfoValidationStep} stub that always return false with INVALID_EMAIL reason.
 * @see UserRegistrationInfoValidationStep
 */
public class InvalidEmailDenyingUserRegistrationInfoValidationStepStub implements UserRegistrationInfoValidationStep {

    @Override
    public ValidationResult validate(UserRegistrationInfo userRegistrationInfo) {
        return ValidationResult.failed(ErrorDetails.INVALID_EMAIL);
    }
}
