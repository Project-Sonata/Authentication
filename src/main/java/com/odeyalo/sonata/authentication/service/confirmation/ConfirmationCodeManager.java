package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeLifecycleHandler;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeActivator;

import java.util.Optional;

/**
 * An extension for {@link ConfirmationCodeGenerator} that add support to manage confirmation code lifecycle.
 * Add capabilities to generate and save, delete, activate a confirmation code
 */
public interface ConfirmationCodeManager extends ConfirmationCodeGenerator, ConfirmationCodeLifecycleHandler, ConfirmationCodeActivator {
    /**
     * Find and return the confirmation code by value
     * @param codeValue - confirmation code value that will be used to find the confirmation code
     * @return - {@link ConfirmationCode} that was found wrapped in Optional, {@link Optional#empty()} otherwise
     */
    Optional<ConfirmationCode> findByCodeValue(String codeValue);

    /**
     * Generate and save the confirmation code to make possible to access it later
     * @param length - length of the code
     * @param lifetimeMinutes - lifetime of the confirmation code
     * @return - generated confirmation code
     */
    @Override
    ConfirmationCode generateCode(User user, int length, int lifetimeMinutes);

    /**
     * Check if code is valid, if so, then ConfirmationCode will be activated and no longer valid.
     * @param codeValue - code value to check
     * @return - {@link ConfirmationCodeCheckResult} that contains info if code is valid or not. Contains error description about what's wrong with the provided code.
     */
    @Override
    ConfirmationCodeCheckResult verifyCodeAndActive(String codeValue);

    /**
     * Delete the confirmation code value
     * @param codeValue - confirmation code to delete
     */
    void deleteCode(String codeValue);

    /**
     * Delete the confirmation code
     * @param code - confirmation code to delete
     */
    void deleteCode(ConfirmationCode code);

}
