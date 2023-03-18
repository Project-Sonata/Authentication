package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;

/**
 * Always return null as MailMessage despite parameters
 */
public class NullConfirmationCodeEmailMessageCreator implements ConfirmationCodeEmailMessageCreator {

    @Override
    public MailMessage createMessage(ConfirmationCode code, EmailReceiver receiver) {
        return null;
    }
}
