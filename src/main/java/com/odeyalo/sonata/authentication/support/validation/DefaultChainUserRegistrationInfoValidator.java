package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Default {@link UserRegistrationInfoValidator} implementation, that uses {@link UserRegistrationInfoValidationStep} to validate the {@link UserRegistrationInfo}
 */
@Component
public class DefaultChainUserRegistrationInfoValidator implements UserRegistrationInfoValidator {
    private final UserRegistrationInfoValidationStepRegistry container;

    public DefaultChainUserRegistrationInfoValidator(UserRegistrationInfoValidationStepRegistry container) {
        this.container = container;
    }

    @Override
    public ValidationResult validateInfo(UserRegistrationInfo info) {
        for (UserRegistrationInfoValidationStep validationStep : container.getSteps()) {
            ValidationResult result = validationStep.validate(info);
            if (!result.isSuccess()) {
                return ValidationResult.failed(result.getErrorDetails());
            }
        }
        return ValidationResult.success();
    }

    public UserRegistrationInfoValidationStepRegistry getContainer() {
        return container;
    }
}
