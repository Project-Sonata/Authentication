package com.odeyalo.sonata.authentication.testing.spy;

import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple spy that do nothing and just used to check if method has been called
 */
public class EmptyEmailConfirmationCodeGeneratorSenderSpy implements EmailConfirmationCodeGeneratorSender {
    private final Logger logger = LoggerFactory.getLogger(EmptyEmailConfirmationCodeGeneratorSenderSpy.class);
    @Getter
    @Accessors(fluent = true)
    private boolean wasSent = false;

    @Override
    public void generateAndSend(EmailReceiver receiver) {
        this.logger.debug("Sent the confirmation code to: {}", receiver);
        this.wasSent = true;
    }
}
