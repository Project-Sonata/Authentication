package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.support.validation.DefaultChainUserRegistrationInfoValidator;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.testing.stubs.InvalidEmailDenyingUserRegistrationInfoValidationStepStub;

import java.util.ArrayList;
import java.util.List;

/**
 * The factory is used to create new {@link DefaultChainUserRegistrationInfoValidator} for testing purposes.
 */
public class DefaultChainUserRegistrationInfoValidatorTestingFactory {

    public static DefaultChainUserRegistrationInfoValidator createEmpty() {
        return new DefaultChainUserRegistrationInfoValidator(new ArrayList<>());
    }

    /**
     * Create {@link DefaultChainUserRegistrationInfoValidator} with already pre-initialized {@link com.odeyalo.sonata.authentication.support.validation.step.ValidationStep}
     *
     * @return - DefaultChainUserRegistrationInfoValidator with registered ValidationSteps
     */
    public static DefaultChainUserRegistrationInfoValidator create() {
        List<UserRegistrationInfoValidationStep> validators = new ArrayList<>();

        validators.add(new InvalidEmailDenyingUserRegistrationInfoValidationStepStub());

        return new DefaultChainUserRegistrationInfoValidator(validators);
    }
}
