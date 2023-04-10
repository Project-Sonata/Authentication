package com.odeyalo.sonata.authentication.support.email.message;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;

/**
 * A helper class that can be used to build email class based on dto or other class
 */
public interface EmailMessageCreatorHelper {
    /**
     * Create the {@link MailMessage} based on provided object.
     * @param obj - object that can be used to build message. Never null
     * @param receiver - email receiver to send message to
     * @return - created {@link MailMessage}
     * @throws IllegalArgumentException - if the obj is invalid class and casting can't be performed.
     */
    MailMessage createMessage(Object obj, EmailReceiver receiver) throws IllegalArgumentException;

    boolean supports(EmailMessageTypeCode type, Object obj);
}
