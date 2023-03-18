package com.odeyalo.sonata.authentication.service.confirmation;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple EmailConfirmationCodeGeneratorSender that only logs the receiver and does nothing else.
 * Useful when there is no need to generate and send a real message to email.
 * Can be used as test-double in tests
 */
public class LoggingNullEmailConfirmationCodeGeneratorSender implements EmailConfirmationCodeGeneratorSender {
    private final Logger logger = LoggerFactory.getLogger(LoggingNullEmailConfirmationCodeGeneratorSender.class);

    @PostConstruct
    public void warnLog() {
        this.logger.warn("This implementation does not generate and send real messages. You need to change the implementation in config to send the real messages");
    }

    @Override
    public void generateAndSend(EmailReceiver receiver) {
        this.logger.info("Sending to the email: {}", receiver);
    }
}
