package com.odeyalo.sonata.authentication.support.validation.step;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Check the provided password by regex.
 * Default regex requires at least 8 characters and 1 number.
 */
@Component
public class PasswordRegexCheckUserRegistrationInfoValidationStep implements UserRegistrationInfoValidationStep {
    private Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$");

    @Override
    public ValidationResult validate(UserRegistrationInfo userRegistrationInfo) {
        if (!pattern.matcher(userRegistrationInfo.getPassword()).matches()) {
            return ValidationResult.failed( ErrorDetails.INVALID_PASSWORD);
        }
        return ValidationResult.success();
    }

    public void overridePattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }
}
