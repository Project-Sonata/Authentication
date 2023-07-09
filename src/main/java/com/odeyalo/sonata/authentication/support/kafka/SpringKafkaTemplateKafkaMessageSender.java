package com.odeyalo.sonata.authentication.support.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SpringKafkaTemplateKafkaMessageSender implements KafkaMessageSender {
    private final KafkaTemplate<String, Object> sender;

    public SpringKafkaTemplateKafkaMessageSender(KafkaTemplate<String, Object> sender) {
        this.sender = sender;
    }

    @Override
    public void send(String topic, Object body) {
        sender.send(topic, body);
        System.out.println(String.format("Sent to: %s with: %s", topic, body));
    }
}
