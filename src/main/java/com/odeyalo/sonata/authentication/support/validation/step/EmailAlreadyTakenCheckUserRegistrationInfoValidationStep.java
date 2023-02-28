package com.odeyalo.sonata.authentication.support.validation.step;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.springframework.stereotype.Component;

/**
 * Simple validation step that checks if the email is already taken by other user
 */
@Component
public class EmailAlreadyTakenCheckUserRegistrationInfoValidationStep implements UserRegistrationInfoValidationStep {

    @Override
    public ValidationResult validate(UserRegistrationInfo userRegistrationInfo) {
        if (userRegistrationInfo.getEmail().equals("alreadytaken@gmail.com")) {
            return ValidationResult.failed( ErrorDetails.EMAIL_ALREADY_TAKEN);
        }
        return ValidationResult.success();
    }
}
