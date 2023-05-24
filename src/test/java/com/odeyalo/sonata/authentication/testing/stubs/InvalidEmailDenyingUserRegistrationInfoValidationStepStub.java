package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

/**
 * Simple {@link UserRegistrationInfoValidationStep} stub that always return false with INVALID_EMAIL reason.
 * @see UserRegistrationInfoValidationStep
 */
public class InvalidEmailDenyingUserRegistrationInfoValidationStepStub implements UserRegistrationInfoValidationStep {

    @Override
    public ValidationResult validate(AdvancedUserRegistrationInfo advancedUserRegistrationInfo) {
        return ValidationResult.failed(ExtendedErrorDetails.INVALID_EMAIL);
    }
}
