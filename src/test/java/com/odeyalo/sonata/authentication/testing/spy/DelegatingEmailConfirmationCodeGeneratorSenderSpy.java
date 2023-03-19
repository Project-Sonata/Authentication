package com.odeyalo.sonata.authentication.testing.spy;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import lombok.Getter;

/**
 * Simple {@link EmailConfirmationCodeGeneratorSender} spy that delegates actual job to parent and just marks that method was actually called
 */
@Getter
public class DelegatingEmailConfirmationCodeGeneratorSenderSpy implements EmailConfirmationCodeGeneratorSender {
    private final EmailConfirmationCodeGeneratorSender delegate;
    private boolean wasSent = false;

    public DelegatingEmailConfirmationCodeGeneratorSenderSpy(EmailConfirmationCodeGeneratorSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void generateAndSend(User user, EmailReceiver receiver) {
        try {
            delegate.generateAndSend(user, receiver);
            wasSent = true;
        } catch (MessageSendingFailedException e) {
            wasSent = false;
            e.printStackTrace();
        }
    }
}
