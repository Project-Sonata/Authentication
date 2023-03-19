package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;

/**
 * Generate the confirmation code and DO NOT save it
 */
public interface ConfirmationCodeGenerator {

    Integer DEFAULT_CONFIRMATION_CODE_LENGTH = 6;

    Integer DEFAULT_CONFIRMATION_CODE_LIFETIME_MINUTES = 6;

    ConfirmationCode generateCode(User user, int length, int lifetimeMinutes);

    default ConfirmationCode generateCode(User user) {
        return generateCode(user, DEFAULT_CONFIRMATION_CODE_LENGTH, DEFAULT_CONFIRMATION_CODE_LIFETIME_MINUTES);
    }
}
