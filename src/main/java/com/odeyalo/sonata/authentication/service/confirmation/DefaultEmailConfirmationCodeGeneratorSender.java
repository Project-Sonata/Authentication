package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Default implementation that just delegate the generation and sending to other classes
 */
public class DefaultEmailConfirmationCodeGeneratorSender implements EmailConfirmationCodeGeneratorSender {
    private final ConfirmationCodeGenerator confirmationCodeGenerator;
    private final ConfirmationCodeEmailMessageCreator bodyCreator;
    private final MailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(DefaultEmailConfirmationCodeGeneratorSender.class);

    public DefaultEmailConfirmationCodeGeneratorSender(ConfirmationCodeGenerator confirmationCodeGenerator, ConfirmationCodeEmailMessageCreator bodyCreator, MailSender mailSender) {
        this.confirmationCodeGenerator = confirmationCodeGenerator;
        this.bodyCreator = bodyCreator;
        this.mailSender = mailSender;
    }

    @Override
    public void generateAndSend(User user, EmailReceiver receiver) throws MessageSendingFailedException {
        Assert.notNull(receiver, "Receiver must be not null!");
        ConfirmationCode confirmationCode = confirmationCodeGenerator.generateCode(user);
        if (confirmationCode == null) {
            this.logger.error("Null was returned as confirmation code, failed to send message to email!");
            throw new MessageSendingFailedException("Failed to send confirmation code since generator returns null");
        }

        MailMessage body = bodyCreator.createMessage(confirmationCode, receiver);
        if (body == null) {
            this.logger.error("Null was returned as message body, failed to send message to email!");
            throw new MessageSendingFailedException("Failed to send confirmation code since body is null");
        }
        mailSender.send(body);
    }
}
