package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.support.event.publisher.EventPublisher;
import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;
import com.odeyalo.sonata.suite.brokers.events.user.data.UserRegisteredEventData;

public class EventPublisherEmailConfirmationManager implements EmailConfirmationManager {
    private final EmailConfirmationManager delegate;
    private final EventPublisher eventPublisher;

    public EventPublisherEmailConfirmationManager(EmailConfirmationManager delegate, EventPublisher eventPublisher) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void sendConfirmationCode(User user, EmailReceiver receiver) throws MessageSendingFailedException {
        delegate.sendConfirmationCode(user, receiver);
    }

    @Override
    public void resendConfirmationCode(User user, EmailReceiver receiver) throws MessageSendingFailedException {
        delegate.resendConfirmationCode(user, receiver);
    }

    @Override
    public ConfirmationCodeCheckResult verifyCode(String codeValue) {
        ConfirmationCodeCheckResult result = delegate.verifyCode(codeValue);
        if (result.isValid()) {
            User user = result.getUser();
            UserRegisteredEventData data = UserRegisteredEventData.of(String.valueOf(user.getId()), user.getEmail());
            eventPublisher.publishEvent(new UserRegisteredEvent(data));
        }
        return result;
    }
}
