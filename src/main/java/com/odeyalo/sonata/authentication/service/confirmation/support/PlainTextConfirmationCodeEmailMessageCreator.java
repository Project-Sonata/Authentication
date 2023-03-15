package com.odeyalo.sonata.authentication.service.confirmation.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import org.springframework.util.Assert;

public class PlainTextConfirmationCodeEmailMessageCreator implements ConfirmationCodeEmailMessageCreator {
    private String subject = "Confirmation code is here!";
    private String bodyFormat = "Your confirmation code is: %s";

    @Override
    public MailMessage createMessage(ConfirmationCode code, EmailReceiver receiver) {
        Assert.notNull(code, "The confirmation code must not be null!");
        Assert.notNull(receiver, "Receiver must not be null!");
        String body = String.format(bodyFormat, code.getCode());
        return new MailMessage(receiver, body.getBytes(), subject);
    }

    public void overrideSubject(String subject) {
        Assert.notNull(subject, "Subject must be not null!");
        this.subject = subject;
    }

    public void overrideBodyFormat(String bodyFormat) {
        Assert.notNull(bodyFormat, "Body format must be not null!");
        this.bodyFormat = bodyFormat;
    }
}