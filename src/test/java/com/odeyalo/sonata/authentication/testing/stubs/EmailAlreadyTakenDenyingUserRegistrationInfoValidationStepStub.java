package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

/**
 * Stub that always return ErrorDetails.EMAIL_ALREADY_TAKEN
 */
public class EmailAlreadyTakenDenyingUserRegistrationInfoValidationStepStub implements UserRegistrationInfoValidationStep {
    @Override
    public ValidationResult validate(AdvancedUserRegistrationInfo advancedUserRegistrationInfo) {
        return ValidationResult.failed(ExtendedErrorDetails.EMAIL_ALREADY_TAKEN);
    }
}
