package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import org.springframework.stereotype.Component;

/**
 * Default {@link UserRegistrationInfoValidator} implementation, that uses {@link UserRegistrationInfoValidationStep} to validate the {@link AdvancedUserRegistrationInfo}
 */
@Component
public class DefaultChainUserRegistrationInfoValidator implements UserRegistrationInfoValidator {
    private final UserRegistrationInfoValidationStepRegistry container;

    public DefaultChainUserRegistrationInfoValidator(UserRegistrationInfoValidationStepRegistry container) {
        this.container = container;
    }

    @Override
    public ValidationResult validateInfo(AdvancedUserRegistrationInfo info) {
        for (UserRegistrationInfoValidationStep validationStep : container.getSteps()) {
            ValidationResult result = validationStep.validate(info);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return ValidationResult.success();
    }

    public UserRegistrationInfoValidationStepRegistry getContainer() {
        return container;
    }
}
