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
        return container.getSteps()
                .stream()
                .map(it -> it.validate(info))
                .filter(ValidationResult::isFailed)
                .findFirst()
                .orElse(ValidationResult.success());
    }
}
