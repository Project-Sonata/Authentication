package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import org.springframework.stereotype.Service;

/**
 * {@link EmailConfirmationManager} implemenetation that ONLY delegates logic to other service.
 * NOTE: This manager DOES NOT do any job, all logic should be written in implementations in constructor.
 *
 */
@Service
public class DelegatingEmailConfirmationManager implements EmailConfirmationManager {
    private final ConfirmationCodeManager manager;
    private final EmailConfirmationCodeGeneratorSender generatorSender;

    public DelegatingEmailConfirmationManager(ConfirmationCodeManager manager, EmailConfirmationCodeGeneratorSender generatorSender) {
        this.manager = manager;
        this.generatorSender = generatorSender;
    }

    @Override
    public void sendConfirmationCode(User user, EmailReceiver receiver) throws MessageSendingFailedException {
        generatorSender.generateAndSend(user, receiver);
    }

    @Override
    public void resendConfirmationCode(User user, EmailReceiver receiver) throws MessageSendingFailedException {
        this.generatorSender.generateAndSend(user, receiver);
    }

    @Override
    public ConfirmationCodeCheckResult verifyCode(String codeValue) {
        return manager.verifyCodeAndActive(codeValue);
    }
}
