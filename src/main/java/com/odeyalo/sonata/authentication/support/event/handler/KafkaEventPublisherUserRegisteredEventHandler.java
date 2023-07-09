package com.odeyalo.sonata.authentication.support.event.handler;

import com.odeyalo.sonata.authentication.support.kafka.KafkaMessageSender;
import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisherUserRegisteredEventHandler implements UserRegisteredEventHandler {
    private final KafkaMessageSender kafkaMessageSender;

    public KafkaEventPublisherUserRegisteredEventHandler(KafkaMessageSender kafkaMessageSender) {
        this.kafkaMessageSender = kafkaMessageSender;
    }

    @Override
    public void onEvent(UserRegisteredEvent body) {
        kafkaMessageSender.send("USER_REGISTERED_TOPIC", body);
    }
}
