package com.odeyalo.sonata.authentication.testing.factory.builders;

import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeManager;
import com.odeyalo.sonata.authentication.service.mfa.handler.EmailMfaMethodHandler;
import com.odeyalo.sonata.authentication.service.mfa.support.EmailMfaConfirmationEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.mfa.support.PlainTextEmailMfaConfirmationEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeManagerTestingFactory;
import com.odeyalo.sonata.authentication.testing.spy.MailSenderSpy;
import lombok.Getter;

@Getter
public class EmailMfaMethodHandlerBuilder {
    private ConfirmationCodeManager confirmationCodeManager = ConfirmationCodeManagerTestingFactory.create();
    private EmailMfaConfirmationEmailMessageCreator creator = new PlainTextEmailMfaConfirmationEmailMessageCreator();
    private MailSender mailSender = new MailSenderSpy();

    public EmailMfaMethodHandlerBuilder overrideConfirmationCodeManager(ConfirmationCodeManager confirmationCodeManager) {
        this.confirmationCodeManager = confirmationCodeManager;
        return this;
    }

    public EmailMfaMethodHandlerBuilder overrideMessageCreator(EmailMfaConfirmationEmailMessageCreator creator) {
        this.creator = creator;
        return this;
    }

    public EmailMfaMethodHandlerBuilder overrideMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
        return this;
    }

    public EmailMfaMethodHandler build() {
        return new EmailMfaMethodHandler(confirmationCodeManager, creator, mailSender);
    }
}
