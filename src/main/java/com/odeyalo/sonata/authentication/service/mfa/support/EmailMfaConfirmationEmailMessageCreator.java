package com.odeyalo.sonata.authentication.service.mfa.support;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageCreatorHelper;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode;

import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.MFA_EMAIL_CONFIRMATION_MESSAGE;

/**
 * Strategy interface to create email message for MFA confirmation purposes
 */
public interface EmailMfaConfirmationEmailMessageCreator extends EmailMessageCreatorHelper {

    @Override
    default MailMessage createMessage(Object obj, EmailReceiver receiver) throws IllegalArgumentException {
        if (!(obj instanceof MfaConfirmationCode)) {
            String message = String.format("Invalid object class type! Expected: %s, actual: %s", MfaConfirmationCode.class.getSimpleName(), obj.getClass().getSimpleName());
            throw new IllegalArgumentException(message);
        }
        return createMessage((MfaConfirmationCode) obj, receiver);
    }

    MailMessage createMessage(MfaConfirmationCode confirmationCode, EmailReceiver emailReceiver);

    @Override
    default boolean supports(EmailMessageTypeCode type, Object obj) {
        return (type == MFA_EMAIL_CONFIRMATION_MESSAGE && obj instanceof MfaConfirmationCode);
    }
}
