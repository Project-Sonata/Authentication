package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.repository.AdvancedUserRegistrationInfoStore;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidator;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.odeyalo.sonata.authentication.service.registration.RegistrationResult.RequiredAction.CONFIRM_EMAIL;

@Component
public class DefaultUserRegistrationManager implements UserRegistrationManager {
    private final UserRegistrationInfoValidator validator;
    private final UserRegistrationService userRegistrationService;
    private final AdvancedUserRegistrationInfoStore infoStore;

    @Autowired
    public DefaultUserRegistrationManager(UserRegistrationInfoValidator validator, UserRegistrationService userRegistrationService, AdvancedUserRegistrationInfoStore infoStore) {
        this.validator = validator;
        this.userRegistrationService = userRegistrationService;
        this.infoStore = infoStore;
    }

    @Override
    public RegistrationResult registerUser(AdvancedUserRegistrationInfo info) {
        Assert.notNull(info, "The UserRegistrationInfo must be not null!");
        ValidationResult result = validator.validateInfo(info);

        if (!result.isSuccess()) {
            RegistrationResult.RequiredAction action = RegistrationResult.RequiredAction.of(result.getErrorDetails().getCode());
            return RegistrationResult.failed(action, result.getErrorDetails());
        }

        RegistrationResult registrationResult = userRegistrationService.registerUser(info);

        if (registrationResult.action() == CONFIRM_EMAIL) {
            infoStore.save(info);
        }

        return registrationResult;
    }
}
