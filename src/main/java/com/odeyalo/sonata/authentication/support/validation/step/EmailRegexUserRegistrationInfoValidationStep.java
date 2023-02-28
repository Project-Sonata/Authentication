package com.odeyalo.sonata.authentication.support.validation.step;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class EmailRegexUserRegistrationInfoValidationStep implements UserRegistrationInfoValidationStep {

    @Override
    public ValidationResult validate(UserRegistrationInfo userRegistrationInfo) {
        String email = userRegistrationInfo.getEmail();
        return EmailValidator.getInstance().isValid(email) ? ValidationResult.success() : ValidationResult.failed(ErrorDetails.INVALID_EMAIL);
    }
}
