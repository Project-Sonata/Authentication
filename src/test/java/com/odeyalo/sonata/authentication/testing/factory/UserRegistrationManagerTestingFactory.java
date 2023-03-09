package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.service.registration.DefaultUserRegistrationManager;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationManager;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationService;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidator;

public class UserRegistrationManagerTestingFactory {

    public static UserRegistrationManager create() {
        return createDefault();
    }

    public static DefaultUserRegistrationManager createDefault() {
        return builderDefault().build();
    }

    public static DefaultUserRegistrationManagerBuilder builderDefault() {
        return new DefaultUserRegistrationManagerBuilder();
    }


    public static class DefaultUserRegistrationManagerBuilder {
        private UserRegistrationInfoValidator validator = UserRegistrationInfoValidatorTestingFactory.createRealValidator();
        private UserRegistrationService userRegistrationService = UserRegistrationServiceTestingFactory.create();

        public DefaultUserRegistrationManagerBuilder overrideValidator(UserRegistrationInfoValidator validator) {
            this.validator = validator;
            return this;
        }

        public DefaultUserRegistrationManagerBuilder overrideRegistrationService(UserRegistrationService userRegistrationService) {
            this.userRegistrationService = userRegistrationService;
            return this;
        }

        public DefaultUserRegistrationManager build() {
            return new DefaultUserRegistrationManager(validator, userRegistrationService);
        }
    }
}
