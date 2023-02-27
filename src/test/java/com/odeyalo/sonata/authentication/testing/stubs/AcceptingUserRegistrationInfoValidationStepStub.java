package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

/**
 * Simple {@link UserRegistrationInfoValidationStep} stub that always returns true.
 * @see UserRegistrationInfoValidationStep
 */
public class AcceptingUserRegistrationInfoValidationStepStub implements UserRegistrationInfoValidationStep {

    @Override
    public ValidationResult validate(UserRegistrationInfo userRegistrationInfo) {
        return ValidationResult.success();
    }
}
