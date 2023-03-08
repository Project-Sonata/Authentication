package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.service.registration.DefaultUserRegistrationManager;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationManager;
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

        public DefaultUserRegistrationManagerBuilder overrideValidator(UserRegistrationInfoValidator validator) {
            this.validator = validator;
            return this;
        }

        public DefaultUserRegistrationManager build() {
            return new DefaultUserRegistrationManager(validator);
        }
    }
}
