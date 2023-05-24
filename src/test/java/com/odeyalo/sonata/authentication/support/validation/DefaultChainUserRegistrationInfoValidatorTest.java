package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationInfoValidatorTestingFactory;
import com.odeyalo.sonata.authentication.testing.stubs.EmailAlreadyTakenDenyingUserRegistrationInfoValidationStepStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test cases for {@link DefaultChainUserRegistrationInfoValidator}
 */
class DefaultChainUserRegistrationInfoValidatorTest {
    public static final String ALREADY_TAKEN_EMAIL_VALUE = "alreadytaken@gmail.com";
    public static final String INVALID_EMAIL = "invalidemail";
    public static final String INVALID_PASSWORD_VALUE = "odeyalo";

    @Test
    @DisplayName("Validate the correct info and expect success")
    void validateCorrectInfo_andExpectSuccess() {
        DefaultChainUserRegistrationInfoValidator validator = UserRegistrationInfoValidatorTestingFactory.createChainedValidator();
        AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
        ValidationResult validationResult = validator.validateInfo(info);
        assertEquals(validationResult, ValidationResult.success());
    }

    @Test
    @DisplayName("Validate invalid email and expect INVALID_EMAIL error")
    void validateInvalidEmail_andExpectInvalidEmailError() {
        DefaultChainUserRegistrationInfoValidator validator = UserRegistrationInfoValidatorTestingFactory.createChainedValidator();
        AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
        info.setEmail(INVALID_EMAIL);
        ValidationResult validationResult = validator.validateInfo(info);

        assertFalse(validationResult.isSuccess());
        assertEquals(ExtendedErrorDetails.INVALID_EMAIL, validationResult.getErrorDetails());
    }


    @Test
    @DisplayName("Validate already taken email and expect EMAIL_ALREADY_TAKEN error")
    void validateUsedEmail_andExpectEmailAlreadyTakenError() {
        DefaultChainUserRegistrationInfoValidator validator = UserRegistrationInfoValidatorTestingFactory.createChainedValidator(
                new EmailAlreadyTakenDenyingUserRegistrationInfoValidationStepStub()
        );
        AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
        info.setEmail(ALREADY_TAKEN_EMAIL_VALUE);
        ValidationResult validationResult = validator.validateInfo(info);

        assertFalse(validationResult.isSuccess());
        assertEquals(ExtendedErrorDetails.EMAIL_ALREADY_TAKEN, validationResult.getErrorDetails());
    }

    @Test
    @DisplayName("Validate invalid password and expect INVALID_PASSWORD error")
    void validateInvalidPassword_andExpectInvalidPasswordError() {
        DefaultChainUserRegistrationInfoValidator validator = UserRegistrationInfoValidatorTestingFactory.createChainedValidator();
        AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
        info.setPassword(INVALID_PASSWORD_VALUE);
        ValidationResult validationResult = validator.validateInfo(info);

        assertFalse(validationResult.isSuccess());
        assertEquals(ExtendedErrorDetails.INVALID_PASSWORD, validationResult.getErrorDetails());
    }

    private AdvancedUserRegistrationInfo getValidUserRegistrationInfo() {
        String email = "odeyalo@gmail.com";
        String password = "mysupercoolpassword123";
        LocalDate birthdate = LocalDate.of(2002, 11, 23);
        String gender = "MALE";
        boolean notificationEnabled = true;

        return AdvancedUserRegistrationInfo.builder()
                .email(email)
                .password(password)
                .birthdate(birthdate)
                .gender(gender)
                .notificationEnabled(notificationEnabled)
                .build();
    }
}
