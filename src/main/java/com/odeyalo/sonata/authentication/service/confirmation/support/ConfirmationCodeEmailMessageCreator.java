package com.odeyalo.sonata.authentication.service.confirmation.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageCreatorHelper;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode;

import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.EMAIL_CONFIRMATION_MESSAGE;

/**
 * A simple interface to create a {@link MailMessage} based on {@link ConfirmationCode}
 */
public interface ConfirmationCodeEmailMessageCreator extends EmailMessageCreatorHelper {

    @Override
    default MailMessage createMessage(Object obj, EmailReceiver emailReceiver) {
        if (!(obj instanceof ConfirmationCode)) {
            String message = String.format("Invalid object class type! Expected: %s, actual: %s", ConfirmationCode.class.getSimpleName(), obj.getClass().getSimpleName());
            throw new IllegalArgumentException(message);
        }
        return createMessage((ConfirmationCode) obj, emailReceiver);
    }

    MailMessage createMessage(ConfirmationCode code, EmailReceiver receiver);


    @Override
    default boolean supports(EmailMessageTypeCode type, Object obj) {
        return (type == EMAIL_CONFIRMATION_MESSAGE && obj instanceof ConfirmationCode);
    }
}
