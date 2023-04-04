package com.odeyalo.sonata.authentication.service.mfa.handler;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.service.mfa.MfaConfirmationSubmission;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;

/**
 * Top-level interface for all mfa methods handlers
 */
public interface MfaMethodHandler {
    /**
     * Starts the multi-factor authentication (MFA) confirmation process for the specified user.
     * This method is responsible for preparation confirmation code(or any other confirmation method) and send it to user.
     * Should be called when the user want to start mfa confirmation.
     * For example, if the implementation responsible for 'email' mfa method, then this implementation
     * should create and send confirmation code to user's email.
     * @param user - The user to start the MFA confirmation process for.
     * @return - MfaMethodInfo object containing information about the MFA method being used for confirmation.
     */
    MfaMethodInfo startMfaConfirmation(User user);

    /**
     * Checks whether the submitted MFA confirmation code is valid.
     * @param submission An object representing the user's MFA submission.
     * @return - true if the confirmation is success, false otherwise
     */
    boolean checkSubmission(MfaConfirmationSubmission submission);

    /**
     * Supported Mfa type by implementation. Never null
     * @return - Supported Mfa type by implementation. Never null
     */
    UserMfaSettings.MfaType supportedMfaType();
}
