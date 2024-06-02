package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.repository.AdvancedUserRegistrationInfoStore;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.support.event.publisher.EventPublisher;
import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;
import com.odeyalo.sonata.suite.brokers.events.user.data.UserRegisteredEventData;

public class EventPublisherEmailConfirmationManager implements EmailConfirmationManager {
    private final EmailConfirmationManager delegate;
    private final EventPublisher eventPublisher;
    private final AdvancedUserRegistrationInfoStore infoStore;

    public EventPublisherEmailConfirmationManager(EmailConfirmationManager delegate, EventPublisher eventPublisher, AdvancedUserRegistrationInfoStore infoStore) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
        this.infoStore = infoStore;
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

        if ( result.isInvalid() ) {
            return result;
        }

        User user = result.getUser();

        AdvancedUserRegistrationInfo info = infoStore.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalStateException("Missing the user registration info!"));

        prepareAndPublishEvent(user, info);
        return result;
    }

    private void prepareAndPublishEvent(User user, AdvancedUserRegistrationInfo info) {
        UserRegisteredEventData eventData = UserRegisteredEventData.builder()
                .email(info.getEmail())
                .countryCode(info.getCountryCode())
                .birthdate(info.getBirthdate())
                .enableNotification(info.isNotificationEnabled())
                .gender(info.getGender())
                .id(String.valueOf(user.getId()))
                .build();

        eventPublisher.publishEvent(new UserRegisteredEvent(eventData));
    }
}
