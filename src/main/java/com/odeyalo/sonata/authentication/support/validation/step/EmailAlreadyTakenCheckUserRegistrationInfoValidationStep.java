package com.odeyalo.sonata.authentication.support.validation.step;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Simple validation step that checks if the email is already taken by other user
 */
@Component
public class EmailAlreadyTakenCheckUserRegistrationInfoValidationStep implements UserRegistrationInfoValidationStep {
    private final UserRepository userRepository;

    @Autowired
    public EmailAlreadyTakenCheckUserRegistrationInfoValidationStep(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ValidationResult validate(AdvancedUserRegistrationInfo advancedUserRegistrationInfo) {
        User user = userRepository.findUserByEmail(advancedUserRegistrationInfo.getEmail());
        return user == null ? ValidationResult.success() : ValidationResult.failed(ExtendedErrorDetails.EMAIL_ALREADY_TAKEN);
    }
}
