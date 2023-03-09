package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.support.validation.DefaultChainUserRegistrationInfoValidator;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidationStepContainer;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidationStepRegistry;
import com.odeyalo.sonata.authentication.support.validation.UserRegistrationInfoValidator;
import com.odeyalo.sonata.authentication.support.validation.step.EmailAlreadyTakenCheckUserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.EmailRegexUserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.PasswordRegexCheckUserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.testing.stubs.InvalidEmailDenyingUserRegistrationInfoValidationStepStub;

import java.util.ArrayList;
import java.util.List;

/**
 * The factory is used to create a new {@link UserRegistrationInfoValidator} for testing purposes.
 */
public class UserRegistrationInfoValidatorTestingFactory {

    public static UserRegistrationInfoValidator createEmpty() {
        return new DefaultChainUserRegistrationInfoValidator(new UserRegistrationInfoValidationStepContainer());
    }

    /**
     * Create {@link DefaultChainUserRegistrationInfoValidator} with already pre-initialized {@link com.odeyalo.sonata.authentication.support.validation.step.ValidationStep}
     *
     * @return - DefaultChainUserRegistrationInfoValidator with registered ValidationSteps
     */
    public static UserRegistrationInfoValidator createDeniedValidator() {
        List<UserRegistrationInfoValidationStep> validators = new ArrayList<>();

        validators.add(new InvalidEmailDenyingUserRegistrationInfoValidationStepStub());

        return new DefaultChainUserRegistrationInfoValidator(new UserRegistrationInfoValidationStepContainer(validators));
    }

    public static UserRegistrationInfoValidator createRealValidator() {
        List<UserRegistrationInfoValidationStep> validators = new ArrayList<>();

        validators.add(new EmailRegexUserRegistrationInfoValidationStep());
        validators.add(new EmailAlreadyTakenCheckUserRegistrationInfoValidationStep());
        validators.add(new PasswordRegexCheckUserRegistrationInfoValidationStep());

        return new DefaultChainUserRegistrationInfoValidator(new UserRegistrationInfoValidationStepContainer(validators));
    }

    public static DefaultChainUserRegistrationInfoValidator createChainedValidator() {
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();
        return new DefaultChainUserRegistrationInfoValidator(registry);
    }
}
