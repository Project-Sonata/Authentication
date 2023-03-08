package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidator;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;

public class DefaultUserRegistrationManager implements UserRegistrationManager {
    private final UserRegistrationInfoValidator validator;

    public DefaultUserRegistrationManager(UserRegistrationInfoValidator validator) {
        this.validator = validator;
    }

    @Override
    public RegistrationResult registerUser(UserRegistrationInfo info) {
        ValidationResult result = validator.validateInfo(info);

        if (!result.isSuccess()) {
            RegistrationResult.RequiredAction action = RegistrationResult.RequiredAction.of(result.getErrorDetails().getCode());
            return RegistrationResult.failed(action, result.getErrorDetails());
        }

        return RegistrationResult.success(RegistrationResult.RequiredAction.CONFIRM_EMAIL);
    }
}
