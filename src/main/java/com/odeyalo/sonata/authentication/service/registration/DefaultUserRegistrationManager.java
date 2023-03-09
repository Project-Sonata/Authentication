package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidator;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserRegistrationManager implements UserRegistrationManager {
    private final UserRegistrationInfoValidator validator;
    private final UserRegistrationService userRegistrationService;

    public DefaultUserRegistrationManager(UserRegistrationInfoValidator validator, UserRegistrationService userRegistrationService) {
        this.validator = validator;
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public RegistrationResult registerUser(UserRegistrationInfo info) {
        ValidationResult result = validator.validateInfo(info);

        if (!result.isSuccess()) {
            RegistrationResult.RequiredAction action = RegistrationResult.RequiredAction.of(result.getErrorDetails().getCode());
            return RegistrationResult.failed(action, result.getErrorDetails());
        }

        return userRegistrationService.registerUser(info);
    }
}
