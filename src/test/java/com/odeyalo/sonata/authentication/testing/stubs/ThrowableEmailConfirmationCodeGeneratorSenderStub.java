package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;

/**
 * Stub that always throws new {@link MessageSendingFailedException} and does nothing else.
 */
public class ThrowableEmailConfirmationCodeGeneratorSenderStub implements EmailConfirmationCodeGeneratorSender {

    @Override
    public void generateAndSend(User user, EmailReceiver receiver) throws MessageSendingFailedException {
        throw new MessageSendingFailedException(String.format("Failed to send the email message to: %s", receiver));
    }
}
