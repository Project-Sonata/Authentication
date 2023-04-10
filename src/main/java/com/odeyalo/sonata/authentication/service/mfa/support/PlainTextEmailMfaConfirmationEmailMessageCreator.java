package com.odeyalo.sonata.authentication.service.mfa.support;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.MFA_EMAIL_CONFIRMATION_MESSAGE;

@Service
@Getter
@Setter
public class PlainTextEmailMfaConfirmationEmailMessageCreator implements EmailMfaConfirmationEmailMessageCreator {
    private String format = "You're trying to login using MFA. Here is your code: %s";
    private String subject = "Your MFA confirmation code";

    @Override
    public MailMessage createMessage(MfaConfirmationCode confirmationCode, EmailReceiver emailReceiver) {
        String message = String.format(this.format, confirmationCode.getConfirmationCode().getCode());

        return MailMessage.builder()
                .messageType(MFA_EMAIL_CONFIRMATION_MESSAGE)
                .content(message.getBytes())
                .receiver(emailReceiver)
                .subject(subject)
                .build();
    }
}
