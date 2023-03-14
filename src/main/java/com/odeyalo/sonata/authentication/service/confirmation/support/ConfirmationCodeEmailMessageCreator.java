package com.odeyalo.sonata.authentication.service.confirmation.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;

/**
 * A simple interface to create a {@link MailMessage} based on {@link ConfirmationCode}
 */
public interface ConfirmationCodeEmailMessageCreator {

    MailMessage createMessage(ConfirmationCode code, EmailReceiver receiver);

}
