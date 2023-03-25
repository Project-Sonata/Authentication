package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;

/**
 * The manager is responsible for managing the confirmation codes that are being sent through email
 */
public interface EmailConfirmationManager {
    /**
     * Generate and send the confirmation code to receiver
     *
     * @param receiver - receiver to send message to
     */
    void sendConfirmationCode(User user, EmailReceiver receiver) throws MessageSendingFailedException;

    /**
     * Re-send the confirmation code to user.
     * The implementation can make previous code invalid, but it's not necessary
     *
     * @param receiver - receiver to receive the confirmation code
     */
    void resendConfirmationCode(User user, EmailReceiver receiver) throws MessageSendingFailedException;

    /**
     * Verify the provided code value.
     * <p>Note: the method SHOULD deactivate the code if the code is valid</p>
     * @param codeValue - code value to verify
     * @return  {@link ConfirmationCodeCheckResult#valid(User)} )} if code is valid, otherwise {@link ConfirmationCodeCheckResult#invalid(String, String)}
     */
    ConfirmationCodeCheckResult verifyCode(String codeValue);
}
