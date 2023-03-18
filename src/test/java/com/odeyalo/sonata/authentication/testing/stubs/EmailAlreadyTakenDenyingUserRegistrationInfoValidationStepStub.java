package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

/**
 * Stub that always return ErrorDetails.EMAIL_ALREADY_TAKEN
 */
public class EmailAlreadyTakenDenyingUserRegistrationInfoValidationStepStub implements UserRegistrationInfoValidationStep {
    @Override
    public ValidationResult validate(UserRegistrationInfo userRegistrationInfo) {
        return ValidationResult.failed(ErrorDetails.EMAIL_ALREADY_TAKEN);
    }
}
