package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;

/**
 * Simple facade interface to generate a confirmation code and send it to specific email.
 */
public interface EmailConfirmationCodeGeneratorSender {
    /**
     * Generate a confirmation code and send it to {@link EmailReceiver}
     * @param receiver - receiver that will receive the confirmation code
     */
    void generateAndSend(EmailReceiver receiver) throws MessageSendingFailedException;

}
