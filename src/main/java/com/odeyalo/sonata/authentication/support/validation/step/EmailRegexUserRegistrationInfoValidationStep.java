package com.odeyalo.sonata.authentication.support.validation.step;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class EmailRegexUserRegistrationInfoValidationStep implements UserRegistrationInfoValidationStep {

    @Override
    public ValidationResult validate(AdvancedUserRegistrationInfo advancedUserRegistrationInfo) {
        String email = advancedUserRegistrationInfo.getEmail();
        return EmailValidator.getInstance().isValid(email) ? ValidationResult.success() : ValidationResult.failed(ExtendedErrorDetails.INVALID_EMAIL);
    }
}
