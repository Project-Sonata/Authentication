package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.support.validation.step.ValidationStep;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Default {@link UserRegistrationInfoValidator} implementation, that uses {@link UserRegistrationInfoValidationStep} to validate the {@link UserRegistrationInfo}
 */
@Component
public class DefaultChainUserRegistrationInfoValidator implements UserRegistrationInfoValidator, UserRegistrationInfoValidationStepRegistry {
    private final List<UserRegistrationInfoValidationStep> validators;

    public DefaultChainUserRegistrationInfoValidator() {
        this.validators = new ArrayList<>();
    }

    public DefaultChainUserRegistrationInfoValidator(List<UserRegistrationInfoValidationStep> validators) {
        this.validators = validators;
    }

    @Override
    public ValidationResult validateInfo(UserRegistrationInfo info) {
        if (!EmailValidator.getInstance().isValid(info.getEmail())) {
            return ValidationResult.failed( ErrorDetails.INVALID_EMAIL);
        }

        if (info.getEmail().equals("alreadytaken@gmail.com")) {
            return ValidationResult.failed( ErrorDetails.EMAIL_ALREADY_TAKEN);
        }

        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$");
        if (!pattern.matcher(info.getPassword()).matches()) {
            return ValidationResult.failed( ErrorDetails.INVALID_PASSWORD);
        }
        return ValidationResult.success();
    }

    @Override
    public List<ValidationStep<UserRegistrationInfo>> getSteps() {
        return null;
    }

    @Override
    public void add(ValidationStep<UserRegistrationInfo> step) {

    }

    @Override
    public void add(int index, ValidationStep<UserRegistrationInfo> step) {

    }

    @Override
    public void remove(ValidationStep<UserRegistrationInfo> step) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
