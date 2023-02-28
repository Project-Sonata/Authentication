package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidationStepContainer;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidationStepRegistry;
import com.odeyalo.sonata.authentication.support.validation.step.EmailAlreadyTakenCheckUserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.EmailRegexUserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.PasswordRegexCheckUserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Create a new {@link UserRegistrationInfoValidationStepRegistry} for testing purposes
 */
public class UserRegistrationInfoValidationStepRegistryTestingFactory {

    public static UserRegistrationInfoValidationStepRegistry createEmpty() {
        return new UserRegistrationInfoValidationStepContainer();
    }

    public static UserRegistrationInfoValidationStepRegistry create() {

        List<UserRegistrationInfoValidationStep> validators = new ArrayList<>();

        validators.add(new EmailRegexUserRegistrationInfoValidationStep());
        validators.add(new EmailAlreadyTakenCheckUserRegistrationInfoValidationStep());
        validators.add(new PasswordRegexCheckUserRegistrationInfoValidationStep());

        return new UserRegistrationInfoValidationStepContainer(validators);
    }

    /**
     * Create a new {@link UserRegistrationInfoValidationStepRegistry} and modify it by {@link Consumer}
     * @param modifier - Consumer to modify the UserRegistrationInfoValidationStepRegistry
     * @return - modified UserRegistrationInfoValidationStepRegistry
     */
    public static UserRegistrationInfoValidationStepRegistry create(Consumer<UserRegistrationInfoValidationStepRegistry> modifier) {
        UserRegistrationInfoValidationStepRegistry parent = create();

        modifier.accept(parent);

        return parent;
    }
}
