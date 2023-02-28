package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link UserRegistrationInfoValidationStepRegistry} that uses List as container.
 */
@Component
public class UserRegistrationInfoValidationStepContainer implements UserRegistrationInfoValidationStepRegistry {
    private final List<UserRegistrationInfoValidationStep> validators;
    private final Logger logger = LoggerFactory.getLogger(UserRegistrationInfoValidationStepContainer.class);

    public UserRegistrationInfoValidationStepContainer() {
        this(new ArrayList<>());
        this.logger.info("Initialized validators with empty list");
    }

    @Autowired
    public UserRegistrationInfoValidationStepContainer(List<UserRegistrationInfoValidationStep> validators) {
        this.validators = validators;
        this.logger.info("Initialized validators with: {}", validators);
    }

    @Override
    public List<UserRegistrationInfoValidationStep> getSteps() {
        return new ArrayList<>(validators);
    }

    @Override
    public void add(UserRegistrationInfoValidationStep step) {
        Assert.notNull(step, "The UserRegistrationInfoValidationStep must be not null!");
        this.validators.add(step);
    }

    @Override
    public void add(int index, UserRegistrationInfoValidationStep step) {
        Assert.notNull(step, "The UserRegistrationInfoValidationStep must be not null!");

        if (index < 0 || index >= validators.size()) {
            this.validators.add(step);
            return;
        }
        this.validators.add(index, step);
    }

    @Override
    public void remove(UserRegistrationInfoValidationStep step) {
        this.validators.remove(step);
    }

    @Override
    public int size() {
        return validators.size();
    }

    @Override
    public void clear() {
        validators.clear();
    }
}
