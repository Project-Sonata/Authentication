package com.odeyalo.sonata.authentication.service.confirmation.support;

/**
 * Responsible for validation and activation the ConfirmationCode, if valid
 */
public interface ConfirmationCodeValidator {
    /**
     * Check if code is valid, if so, then ConfirmationCode will be activated and no longer valid.
     * @param codeValue - code value to check
     * @return - {@link ConfirmationCodeCheckResult} that contains info if code is valid or not. Contains error description about what's wrong with the provided code.
     */
    ConfirmationCodeCheckResult verifyCodeAndActive(String codeValue);
}
